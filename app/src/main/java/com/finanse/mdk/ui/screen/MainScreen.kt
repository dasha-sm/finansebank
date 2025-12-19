package com.finanse.mdk.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.finanse.mdk.data.model.User
import com.finanse.mdk.ui.components.BottomNavigationBar
import com.finanse.mdk.ui.navigation.Screen
import com.finanse.mdk.ui.viewmodel.AuthViewModel
import com.finanse.mdk.ui.viewmodel.FinancialGoalViewModel
import com.finanse.mdk.ui.viewmodel.MainViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    mainViewModel: MainViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    goalViewModel: FinancialGoalViewModel = hiltViewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val balance by mainViewModel.balance.collectAsState()
    val totalIncome by mainViewModel.totalIncome.collectAsState()
    val totalExpense by mainViewModel.totalExpense.collectAsState()
    val transactions by mainViewModel.transactions.collectAsState()
    val activeGoals by goalViewModel.activeGoals.collectAsState()
    
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            mainViewModel.setUserId(user.id)
            goalViewModel.loadActiveGoals(user.id)
        }
    }
    
    val formatter = NumberFormat.getCurrencyInstance(Locale("ru", "RU"))
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Главная") },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Profile.route) }) {
                        Icon(Icons.Default.Person, contentDescription = "Профиль")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddTransaction.route) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Добавить операцию")
            }
        },
        bottomBar = {
            BottomNavigationBar(navController = navController, currentRoute = Screen.Main.route)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Баланс
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Баланс",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatter.format(balance),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (balance >= 0) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.error
                    )
                }
            }
            
            // Доходы и расходы
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Доходы",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = formatter.format(totalIncome),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Расходы",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = formatter.format(totalExpense),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            // Последние операции
            Text(
                text = "Последние операции",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            
            if (transactions.isEmpty()) {
                Text(
                    text = "Нет операций",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(32.dp)
                )
            } else {
                transactions.take(5).forEach { transaction ->
                    TransactionItem(
                        transaction = transaction,
                        onClick = { navController.navigate(Screen.Transactions.route) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            
            // Активные финансовые цели
            if (activeGoals.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Активные цели",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
                activeGoals.take(3).forEach { goal ->
                    val progress = (goal.currentAmount / goal.targetAmount).coerceIn(0.0, 1.0)
                    Card(
                        onClick = { navController.navigate(Screen.Goals.route) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = goal.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${(progress * 100).toInt()}%",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            LinearProgressIndicator(
                                progress = progress.toFloat(),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                text = "${formatter.format(goal.currentAmount)} / ${formatter.format(goal.targetAmount)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            
            // Кнопка админ-панели для администраторов
            currentUser?.let { user ->
                if (user.role == com.finanse.mdk.data.model.UserRole.ADMIN) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { navController.navigate(Screen.Admin.route) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Админ-панель")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionItem(
    transaction: com.finanse.mdk.data.model.Transaction,
    onClick: () -> Unit
) {
    val formatter = NumberFormat.getCurrencyInstance(Locale("ru", "RU"))
    val isIncome = transaction.type == com.finanse.mdk.data.model.TransactionType.INCOME
    
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.description.ifEmpty { "Операция" },
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = java.text.SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                        .format(java.util.Date(transaction.date)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "${if (isIncome) "+" else "-"}${formatter.format(transaction.amount)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isIncome) 
                    MaterialTheme.colorScheme.primary 
                else 
                    MaterialTheme.colorScheme.error
            )
        }
    }
}

