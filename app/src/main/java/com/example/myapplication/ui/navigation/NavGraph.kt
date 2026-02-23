package com.example.myapplication.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myapplication.ui.screens.AddExpenseScreen
import com.example.myapplication.ui.screens.HomeScreen
import com.example.myapplication.ui.screens.ProfileScreen
import com.example.myapplication.ui.screens.SettingsScreen
import com.example.myapplication.ui.screens.StatisticsScreen
import com.example.myapplication.ui.viewmodel.ExpenseViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: ExpenseViewModel,
    onShowNotification: (String) -> Unit
) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                navController = navController,
                viewModel = viewModel,
                onShowNotification = onShowNotification
            )
        }
        composable("add_expense") {
            AddExpenseScreen(
                navController = navController,
                viewModel = viewModel,
                onShowNotification = onShowNotification
            )
        }
        composable("settings") {
            SettingsScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        composable("profile") {
            ProfileScreen(navController = navController)
        }
        composable("statistics") {
            StatisticsScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
    }
}
