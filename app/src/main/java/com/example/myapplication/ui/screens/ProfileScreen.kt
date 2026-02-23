package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavHostController) {
    var showLicenses by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("О приложении") },
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App Icon Placeholder
            Card(
                modifier = Modifier.size(120.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Expense Tracker",
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                "Версия 1.0.0",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Описание
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "О приложении",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Expense Tracker - это удобное приложение для учета личных расходов. " +
                        "Отслеживайте свои траты, устанавливайте лимиты и анализируйте " +
                        "свои финансовые привычки с помощью наглядных графиков.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Функции
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Возможности",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FeatureItem(Icons.Default.Add, "Добавление расходов")
                    FeatureItem(Icons.Default.Edit, "Редактирование и удаление")
                    FeatureItem(Icons.Default.PieChart, "Категории расходов")
                    FeatureItem(Icons.Default.BarChart, "Графики и статистика")
                    FeatureItem(Icons.Default.Notifications, "Уведомления о лимитах")
                    FeatureItem(Icons.Default.Search, "Поиск и фильтрация")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Лицензии
            Button(
                onClick = { showLicenses = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Description, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Лицензии и благодарности")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Copyright
            Text(
                "© 2024 Expense Tracker\nВсе права защищены",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }

    // Диалог лицензий
    if (showLicenses) {
        AlertDialog(
            onDismissRequest = { showLicenses = false },
            title = { Text("Лицензии") },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    LicenseItem(
                        "AndroidX",
                        "Apache License 2.0",
                        "Библиотеки для разработки Android-приложений"
                    )
                    HorizontalDivider()
                    LicenseItem(
                        "Jetpack Compose",
                        "Apache License 2.0",
                        "Современный UI toolkit для Android"
                    )
                    HorizontalDivider()
                    LicenseItem(
                        "Room",
                        "Apache License 2.0",
                        "Библиотека для работы с базой данных"
                    )
                    HorizontalDivider()
                    LicenseItem(
                        "Kotlin",
                        "Apache License 2.0",
                        "Язык программирования"
                    )
                    HorizontalDivider()
                    LicenseItem(
                        "Vico",
                        "Apache License 2.0",
                        "Библиотека для создания графиков"
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showLicenses = false }) {
                    Text("Закрыть")
                }
            }
        )
    }
}

@Composable
private fun FeatureItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun LicenseItem(name: String, license: String, description: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(name, style = MaterialTheme.typography.titleSmall)
        Text(license, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
        Text(description, style = MaterialTheme.typography.bodySmall)
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
    }
}
