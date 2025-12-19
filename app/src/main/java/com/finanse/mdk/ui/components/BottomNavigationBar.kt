package com.finanse.mdk.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.finanse.mdk.ui.navigation.Screen

@Composable
fun BottomNavigationBar(
    navController: NavController,
    currentRoute: String
) {
    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == Screen.Main.route,
            onClick = { 
                if (currentRoute != Screen.Main.route) {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Main.route) { inclusive = true }
                    }
                }
            },
            icon = { Icon(Icons.Default.Home, contentDescription = "Главная") },
            label = { Text("Главная") }
        )
        NavigationBarItem(
            selected = currentRoute == Screen.Transactions.route,
            onClick = { 
                if (currentRoute != Screen.Transactions.route) {
                    navController.navigate(Screen.Transactions.route)
                }
            },
            icon = { Icon(Icons.Default.List, contentDescription = "Операции") },
            label = { Text("Операции") }
        )
        NavigationBarItem(
            selected = currentRoute == Screen.Statistics.route,
            onClick = { 
                if (currentRoute != Screen.Statistics.route) {
                    navController.navigate(Screen.Statistics.route)
                }
            },
            icon = { Icon(Icons.Default.BarChart, contentDescription = "Статистика") },
            label = { Text("Статистика") }
        )
        NavigationBarItem(
            selected = currentRoute == Screen.Categories.route,
            onClick = { 
                if (currentRoute != Screen.Categories.route) {
                    navController.navigate(Screen.Categories.route)
                }
            },
            icon = { Icon(Icons.Default.Category, contentDescription = "Категории") },
            label = { Text("Категории") }
        )
        NavigationBarItem(
            selected = currentRoute == Screen.Goals.route,
            onClick = { 
                if (currentRoute != Screen.Goals.route) {
                    navController.navigate(Screen.Goals.route)
                }
            },
            icon = { Icon(Icons.Default.Flag, contentDescription = "Цели") },
            label = { Text("Цели") }
        )
    }
}




