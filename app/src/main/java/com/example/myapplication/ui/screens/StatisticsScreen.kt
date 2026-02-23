package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myapplication.data.model.ExpenseCategory
import com.example.myapplication.data.repository.Currencies
import com.example.myapplication.ui.viewmodel.ExpenseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    navController: NavHostController,
    viewModel: ExpenseViewModel
) {
    val currencyInfo by viewModel.currentCurrencyInfo.collectAsState(initial = Currencies.ALL.first())
    val categoryStats by viewModel.getCategoryExpensesForChart().collectAsState(initial = emptyMap())
    val dailyStats by viewModel.getDailyExpensesForChart().collectAsState(initial = emptyMap())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Статистика") },
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
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // График по категориям (Pie Chart)
            if (categoryStats.isNotEmpty()) {
                Text(
                    "Распределение по категориям",
                    style = MaterialTheme.typography.titleLarge
                )

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        SimplePieChart(
                            categoryStats = categoryStats,
                            currencySymbol = currencyInfo.symbol
                        )
                    }
                }
            }

            // График по дням (Bar Chart)
            if (dailyStats.isNotEmpty() && dailyStats.size > 1) {
                Text(
                    "Расходы по дням",
                    style = MaterialTheme.typography.titleLarge
                )

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SimpleBarChart(
                            dailyStats = dailyStats,
                            currencySymbol = currencyInfo.symbol
                        )
                    }
                }
            }

            // Список категорий с суммами
            if (categoryStats.isNotEmpty()) {
                Text(
                    "Детализация по категориям",
                    style = MaterialTheme.typography.titleLarge
                )

                categoryStats.entries.sortedByDescending { it.value }.forEach { (category, amount) ->
                    val expenseCategory = ExpenseCategory.fromString(category)
                    CategoryStatItem(
                        category = expenseCategory,
                        amount = amount,
                        currencySymbol = currencyInfo.symbol,
                        total = categoryStats.values.sum()
                    )
                }
            }
        }
    }
}

@Composable
private fun SimplePieChart(
    categoryStats: Map<String, Double>,
    currencySymbol: String
) {
    val total = categoryStats.values.sum()
    val sortedStats = categoryStats.entries.sortedByDescending { it.value }

    // Круговой график с секторами
    Box(
        modifier = Modifier
            .size(200.dp)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Простая визуализация - круги разного размера
        sortedStats.forEachIndexed { index, (category, amount) ->
            val expenseCategory = ExpenseCategory.fromString(category)
            val percentage = (amount / total * 100).toInt()
            val size = 120 - (index * 20)

            Box(
                modifier = Modifier
                    .size(size.dp)
                    .clip(CircleShape)
                    .background(expenseCategory.color.copy(alpha = 0.7f))
            )
        }

        // Центральный текст
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "Всего",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                "${total.toInt()} $currencySymbol",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }

    // Легенда
    Column(modifier = Modifier.padding(top = 8.dp)) {
        sortedStats.forEach { (category, amount) ->
            val expenseCategory = ExpenseCategory.fromString(category)
            val percentage = (amount / total * 100).toInt()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(expenseCategory.color)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    expenseCategory.displayName,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    "${amount.toInt()} $currencySymbol ($percentage%)",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun SimpleBarChart(
    dailyStats: Map<String, Double>,
    currencySymbol: String
) {
    val maxValue = dailyStats.values.maxOrNull() ?: 1.0
    val entriesList = dailyStats.toList()
    val lastEntries = if (entriesList.size > 7) entriesList.subList(entriesList.size - 7, entriesList.size) else entriesList

    Column {
        lastEntries.forEach { entry ->
            val date = entry.first
            val amount = entry.second
            val percentage = (amount / maxValue).toFloat()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    date,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.width(60.dp)
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(24.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(percentage.coerceIn(0.05f, 1f))
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "${amount.toInt()}",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.End,
                    modifier = Modifier.width(60.dp)
                )
            }
        }
    }
}

@Composable
private fun CategoryStatItem(
    category: ExpenseCategory,
    amount: Double,
    currencySymbol: String,
    total: Double
) {
    val percentage = if (total > 0) (amount / total * 100).toInt() else 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = category.icon,
                contentDescription = null,
                tint = category.color,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(category.displayName, style = MaterialTheme.typography.bodyLarge)
                LinearProgressIndicator(
                    progress = { percentage / 100f },
                    modifier = Modifier.fillMaxWidth(),
                    color = category.color
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${amount.toInt()} $currencySymbol",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    "$percentage%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
