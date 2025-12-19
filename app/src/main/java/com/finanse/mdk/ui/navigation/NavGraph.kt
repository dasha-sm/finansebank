package com.finanse.mdk.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.finanse.mdk.ui.screen.*

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Auth.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Auth.route) {
            AuthScreen(navController = navController)
        }
        composable(Screen.Main.route) {
            MainScreen(navController = navController)
        }
        composable(Screen.Transactions.route) {
            TransactionsScreen(navController = navController)
        }
        composable(Screen.AddTransaction.route) {
            AddTransactionScreen(navController = navController)
        }
        composable(Screen.Categories.route) {
            CategoriesScreen(navController = navController)
        }
        composable(Screen.Statistics.route) {
            StatisticsScreen(navController = navController)
        }
        composable(Screen.Budgets.route) {
            BudgetsScreen(navController = navController)
        }
        composable(Screen.Goals.route) {
            FinancialGoalsScreen(navController = navController)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }
        composable(Screen.Admin.route) {
            AdminScreen(navController = navController)
        }
    }
}

sealed class Screen(val route: String) {
    object Auth : Screen("auth")
    object Main : Screen("main")
    object Transactions : Screen("transactions")
    object AddTransaction : Screen("add_transaction")
    object Categories : Screen("categories")
    object Statistics : Screen("statistics")
    object Budgets : Screen("budgets")
    object Goals : Screen("goals")
    object Profile : Screen("profile")
    object Admin : Screen("admin")
}


