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
import com.example.myapplication.data.repository.Currencies
import com.example.myapplication.ui.viewmodel.ExpenseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController,
    viewModel: ExpenseViewModel
) {
    val currency by viewModel.currency.collectAsState(initial = "UZS")
    val monthlyLimit by viewModel.monthlyLimit.collectAsState(initial = 1000000.0)
    val dailyLimit by viewModel.dailyLimit.collectAsState(initial = 50000.0)
    val currencyInfo by viewModel.currentCurrencyInfo.collectAsState(initial = Currencies.ALL.first())

    var showCurrencyDropdown by remember { mutableStateOf(false) }
    var monthlyLimitText by remember { mutableStateOf(monthlyLimit.toInt().toString()) }
    var dailyLimitText by remember { mutableStateOf(dailyLimit.toInt().toString()) }

    // Обновляем текст при изменении значений
    LaunchedEffect(monthlyLimit) {
        monthlyLimitText = monthlyLimit.toInt().toString()
    }
    LaunchedEffect(dailyLimit) {
        dailyLimitText = dailyLimit.toInt().toString()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Настройки") },
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
            // Валюта
            Text("Валюта", style = MaterialTheme.typography.titleMedium)
            ExposedDropdownMenuBox(
                expanded = showCurrencyDropdown,
                onExpandedChange = { showCurrencyDropdown = it }
            ) {
                OutlinedTextField(
                    value = "${currencyInfo.flag} ${currencyInfo.name} (${currencyInfo.symbol})",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Текущая валюта") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCurrencyDropdown) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryEditable, true)
                )
                ExposedDropdownMenu(
                    expanded = showCurrencyDropdown,
                    onDismissRequest = { showCurrencyDropdown = false }
                ) {
                    Currencies.ALL.forEach { curr ->
                        DropdownMenuItem(
                            text = {
                                Row {
                                    Text(curr.flag)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("${curr.name} (${curr.symbol})")
                                }
                            },
                            onClick = {
                                viewModel.setCurrency(curr.code)
                                showCurrencyDropdown = false
                            }
                        )
                    }
                }
            }

            HorizontalDivider()

            // Лимиты
            Text("Лимиты расходов", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = monthlyLimitText,
                onValueChange = {
                    monthlyLimitText = it
                    it.toDoubleOrNull()?.let { value ->
                        viewModel.setMonthlyLimit(value)
                    }
                },
                label = { Text("Месячный лимит (${currencyInfo.symbol})") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = dailyLimitText,
                onValueChange = {
                    dailyLimitText = it
                    it.toDoubleOrNull()?.let { value ->
                        viewModel.setDailyLimit(value)
                    }
                },
                label = { Text("Дневной лимит (${currencyInfo.symbol})") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider()

            // Информация
            Text("Информация", style = MaterialTheme.typography.titleMedium)
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Версия: 1.0", style = MaterialTheme.typography.bodyMedium)
                    Text("Приложение для учета расходов", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
