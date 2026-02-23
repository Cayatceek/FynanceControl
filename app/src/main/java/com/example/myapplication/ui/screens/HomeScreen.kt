package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myapplication.data.entity.Expense
import com.example.myapplication.data.model.ExpenseCategory
import com.example.myapplication.data.repository.Currencies
import com.example.myapplication.ui.viewmodel.ExpenseViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: ExpenseViewModel,
    onShowNotification: (String) -> Unit
) {
    val expenses by viewModel.filteredExpenses.collectAsState(initial = emptyList())
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val currencyInfo by viewModel.currentCurrencyInfo.collectAsState(initial = Currencies.ALL.first())
    val monthlyLimit by viewModel.monthlyLimit.collectAsState(initial = 1000000.0)
    val dailyLimit by viewModel.dailyLimit.collectAsState(initial = 50000.0)

    var dailyTotal by remember { mutableStateOf(0.0) }
    var monthlyTotal by remember { mutableStateOf(0.0) }
    var showDeleteDialog by remember { mutableStateOf<Expense?>(null) }
    var showEditExpense by remember { mutableStateOf<Expense?>(null) }
    var selectedTab by remember { mutableStateOf(0) }

    // Load statistics
    LaunchedEffect(Unit) {
        dailyTotal = viewModel.getDailyStats()
        monthlyTotal = viewModel.getMonthlyStats()
    }

    // Refresh stats when expenses change
    LaunchedEffect(expenses) {
        dailyTotal = viewModel.getDailyStats()
        monthlyTotal = viewModel.getMonthlyStats()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Обзор расходов") },
                actions = {
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(Icons.Default.Settings, contentDescription = "Настройки")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Главная") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.BarChart, contentDescription = null) },
                    label = { Text("Статистика") },
                    selected = selectedTab == 1,
                    onClick = { navController.navigate("statistics") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Профиль") },
                    selected = selectedTab == 2,
                    onClick = { navController.navigate("profile") }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("add_expense") }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Добавить расход")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::setSearchQuery,
                placeholder = { Text("Поиск расходов...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                singleLine = true
            )

            // Statistics Card
            StatisticsCard(
                daily = dailyTotal,
                monthly = monthlyTotal,
                dailyLimit = dailyLimit,
                monthlyLimit = monthlyLimit,
                currencySymbol = currencyInfo.symbol
            )

            // Category Filter Chips
            CategoryFilterChips(
                selectedCategory = selectedCategory,
                onCategorySelected = viewModel::setCategoryFilter
            )

            // Expenses List Header
            Text(
                "Расходы (${expenses.size}):",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Expenses List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(expenses, key = { it.id }) { expense ->
                    ExpenseItem(
                        expense = expense,
                        currencySymbol = currencyInfo.symbol,
                        onEdit = { showEditExpense = expense },
                        onDelete = { showDeleteDialog = expense }
                    )
                }
            }
        }
    }

    // Delete Confirmation Dialog
    showDeleteDialog?.let { expense ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Удалить расход?") },
            text = { Text("Вы уверены, что хотите удалить расход \"${expense.description}\" на ${expense.amount.toInt()} руб.?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteExpense(expense)
                        showDeleteDialog = null
                    }
                ) {
                    Text("Удалить", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Отмена")
                }
            }
        )
    }

    // Edit Expense Dialog (using the AddExpenseScreen as a dialog)
    showEditExpense?.let { expense ->
        EditExpenseDialog(
            expense = expense,
            viewModel = viewModel,
            onDismiss = { showEditExpense = null },
            onShowNotification = onShowNotification
        )
    }
}

@Composable
fun StatisticsCard(
    daily: Double,
    monthly: Double,
    dailyLimit: Double,
    monthlyLimit: Double,
    currencySymbol: String
) {
    val monthlyProgress = (monthly / monthlyLimit).coerceIn(0.0, 1.0).toFloat()
    val dailyProgress = (daily / dailyLimit).coerceIn(0.0, 1.0).toFloat()
    val isMonthlyOverLimit = monthly > monthlyLimit
    val isDailyOverLimit = daily > dailyLimit

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Дневная статистика
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Сегодня:", style = MaterialTheme.typography.titleMedium)
                Text(
                    "${daily.toInt()} $currencySymbol",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isDailyOverLimit) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                )
            }
            LinearProgressIndicator(
                progress = { dailyProgress },
                modifier = Modifier.fillMaxWidth(),
                color = if (isDailyOverLimit) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary,
            )
            Text(
                "Лимит: ${dailyLimit.toInt()} $currencySymbol",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Месячная статистика
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Месяц:", style = MaterialTheme.typography.titleLarge)
                Text(
                    "${monthly.toInt()} $currencySymbol",
                    style = MaterialTheme.typography.titleLarge,
                    color = if (isMonthlyOverLimit) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            }
            LinearProgressIndicator(
                progress = { monthlyProgress },
                modifier = Modifier.fillMaxWidth(),
                color = if (isMonthlyOverLimit) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
            )
            Text(
                "Лимит: ${monthlyLimit.toInt()} $currencySymbol",
                style = MaterialTheme.typography.bodyMedium,
                color = if (isMonthlyOverLimit) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (isMonthlyOverLimit) {
                Text(
                    "Превышен месячный лимит!",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun CategoryFilterChips(
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedCategory == null,
            onClick = { onCategorySelected(null) },
            label = { Text("Все") }
        )

        ExpenseCategory.getAll().forEach { category ->
            FilterChip(
                selected = selectedCategory == category.displayName,
                onClick = { onCategorySelected(category.displayName) },
                label = { Text(category.displayName) },
                leadingIcon = {
                    Icon(
                        imageVector = category.icon,
                        contentDescription = null,
                        tint = category.color,
                        modifier = Modifier.size(18.dp)
                    )
                }
            )
        }
    }
}

@Composable
fun ExpenseItem(
    expense: Expense,
    currencySymbol: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val category = ExpenseCategory.fromString(expense.category)
    val dateFormat = remember { SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Icon
            Icon(
                imageVector = category.icon,
                contentDescription = null,
                tint = category.color,
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Expense Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    expense.description,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    "${expense.amount.toInt()} $currencySymbol • ${category.displayName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    dateFormat.format(expense.date),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Actions
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Редактировать")
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Удалить",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditExpenseDialog(
    expense: Expense,
    viewModel: ExpenseViewModel,
    onDismiss: () -> Unit,
    onShowNotification: (String) -> Unit
) {
    var amount by remember { mutableStateOf(expense.amount.toString()) }
    var description by remember { mutableStateOf(expense.description) }
    var selectedCategory by remember { mutableStateOf(expense.category) }
    var showCategoryDropdown by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Редактировать расход") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Сумма") },
                    singleLine = true
                )

                ExposedDropdownMenuBox(
                    expanded = showCategoryDropdown,
                    onExpandedChange = { showCategoryDropdown = it }
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Категория") },
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable, true)
                    )
                    ExposedDropdownMenu(
                        expanded = showCategoryDropdown,
                        onDismissRequest = { showCategoryDropdown = false }
                    ) {
                        ExpenseCategory.getAll().forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.displayName) },
                                onClick = {
                                    selectedCategory = category.displayName
                                    showCategoryDropdown = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Описание") },
                    minLines = 2
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val updatedExpense = expense.copy(
                        amount = amount.toDoubleOrNull() ?: expense.amount,
                        category = selectedCategory,
                        description = description
                    )
                    viewModel.editExpense(updatedExpense, onShowNotification)
                    onDismiss()
                }
            ) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}
