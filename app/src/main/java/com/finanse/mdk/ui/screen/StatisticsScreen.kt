package com.finanse.mdk.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.finanse.mdk.ui.components.BottomNavigationBar
import com.finanse.mdk.ui.navigation.Screen
import com.finanse.mdk.ui.viewmodel.AuthViewModel
import com.finanse.mdk.ui.viewmodel.StatisticsPeriod
import com.finanse.mdk.ui.viewmodel.StatisticsViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    navController: NavController,
    statisticsViewModel: StatisticsViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val totalIncome by statisticsViewModel.totalIncome.collectAsState()
    val totalExpense by statisticsViewModel.totalExpense.collectAsState()
    val categoryExpenses by statisticsViewModel.categoryExpenses.collectAsState()
    val categoryNames by statisticsViewModel.categoryNames.collectAsState()
    
    var selectedPeriod by remember { mutableStateOf(StatisticsPeriod.MONTH) }
    
    LaunchedEffect(currentUser, selectedPeriod) {
        currentUser?.let { user ->
            statisticsViewModel.loadStatistics(user.id, selectedPeriod)
        }
    }
    
    val formatter = NumberFormat.getCurrencyInstance(Locale("ru", "RU"))
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Статистика") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController, currentRoute = Screen.Statistics.route)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Период
            Text("Период", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedPeriod == StatisticsPeriod.WEEK,
                    onClick = { selectedPeriod = StatisticsPeriod.WEEK },
                    label = { Text("Неделя") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = selectedPeriod == StatisticsPeriod.MONTH,
                    onClick = { selectedPeriod = StatisticsPeriod.MONTH },
                    label = { Text("Месяц") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = selectedPeriod == StatisticsPeriod.YEAR,
                    onClick = { selectedPeriod = StatisticsPeriod.YEAR },
                    label = { Text("Год") },
                    modifier = Modifier.weight(1f)
                )
            }
            
            // Итоги
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Доходы:", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            text = formatter.format(totalIncome),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Расходы:", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            text = formatter.format(totalExpense),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    Divider()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Итого:", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = formatter.format(totalIncome - totalExpense),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            // Расходы по категориям
            if (categoryExpenses.isNotEmpty()) {
                Text("Расходы по категориям", style = MaterialTheme.typography.titleMedium)
                categoryExpenses.entries.sortedByDescending { it.value }.forEach { (categoryId, amount) ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                categoryNames[categoryId] ?: "Неизвестная категория", 
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = formatter.format(amount),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

