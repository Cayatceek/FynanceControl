package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myapplication.data.entity.Expense
import com.example.myapplication.data.model.ExpenseCategory
import com.example.myapplication.ui.viewmodel.ExpenseViewModel
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    navController: NavHostController,
    viewModel: ExpenseViewModel,
    onShowNotification: (String) -> Unit,
    expenseToEdit: Expense? = null
) {
    var amount by remember { mutableStateOf(expenseToEdit?.amount?.toString() ?: "") }
    var description by remember { mutableStateOf(expenseToEdit?.description ?: "") }
    var selectedCategory by remember { mutableStateOf(expenseToEdit?.category ?: ExpenseCategory.FOOD.displayName) }
    var showCategoryDropdown by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    val isEditing = expenseToEdit != null
    val title = if (isEditing) "Редактировать расход" else "Добавить расход"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Amount input
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Сумма (руб.)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            // Category dropdown
            ExposedDropdownMenuBox(
                expanded = showCategoryDropdown,
                onExpandedChange = { showCategoryDropdown = it }
            ) {
                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Категория") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCategoryDropdown) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryEditable, true)
                )
                ExposedDropdownMenu(
                    expanded = showCategoryDropdown,
                    onDismissRequest = { showCategoryDropdown = false }
                ) {
                    ExpenseCategory.getAll().forEach { category ->
                        DropdownMenuItem(
                            text = {
                                Row {
                                    Icon(
                                        imageVector = category.icon,
                                        contentDescription = null,
                                        tint = category.color,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(category.displayName)
                                }
                            },
                            onClick = {
                                selectedCategory = category.displayName
                                showCategoryDropdown = false
                            }
                        )
                    }
                }
            }

            // Description input
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Описание") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.weight(1f))

            // Save button
            Button(
                onClick = { showConfirmDialog = true },
                modifier = Modifier.fillMaxWidth(),
                enabled = amount.isNotBlank() && description.isNotBlank()
            ) {
                Text(if (isEditing) "Сохранить изменения" else "Добавить расход")
            }
        }
    }

    // Confirmation dialog
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Подтверждение") },
            text = { Text("${if (isEditing) "Сохранить изменения" else "Добавить"} расход на ${amount} руб. для категории \"$selectedCategory\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        val expenseAmount = amount.toDoubleOrNull() ?: 0.0
                        val expense = Expense(
                            id = expenseToEdit?.id ?: 0,
                            amount = expenseAmount,
                            category = selectedCategory,
                            description = description,
                            date = expenseToEdit?.date ?: Date()
                        )

                        if (isEditing) {
                            viewModel.editExpense(expense, onShowNotification)
                        } else {
                            viewModel.addExpense(expense, onShowNotification)
                        }
                        showConfirmDialog = false
                        navController.popBackStack()
                    }
                ) {
                    Text("Подтвердить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}
