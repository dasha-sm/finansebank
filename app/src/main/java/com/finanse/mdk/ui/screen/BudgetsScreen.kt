package com.finanse.mdk.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.finanse.mdk.data.model.BudgetPeriod
import com.finanse.mdk.ui.viewmodel.AuthViewModel
import com.finanse.mdk.ui.viewmodel.BudgetViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetsScreen(
    navController: NavController,
    budgetViewModel: BudgetViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val budgets by budgetViewModel.budgets.collectAsState()
    val uiState by budgetViewModel.uiState.collectAsState()
    
    var showAddDialog by remember { mutableStateOf(false) }
    var amount by remember { mutableStateOf("") }
    var selectedPeriod by remember { mutableStateOf<BudgetPeriod?>(null) }
    
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            budgetViewModel.loadBudgets(user.id)
        }
    }
    
    val formatter = NumberFormat.getCurrencyInstance(Locale("ru", "RU"))
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Бюджеты") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Добавить")
                    }
                }
            )
        }
    ) { padding ->
        if (budgets.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text("Нет бюджетов")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(budgets) { budget ->
                    BudgetItem(
                        budget = budget,
                        onDelete = { budgetViewModel.deleteBudget(budget) }
                    )
                }
            }
        }
        
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Добавить бюджет") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = amount,
                            onValueChange = { amount = it },
                            label = { Text("Сумма") },
                            enabled = true,
                            readOnly = false,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Период", style = MaterialTheme.typography.titleSmall)
                        Row {
                            FilterChip(
                                selected = selectedPeriod == BudgetPeriod.WEEKLY,
                                onClick = { selectedPeriod = BudgetPeriod.WEEKLY },
                                label = { Text("Неделя") }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            FilterChip(
                                selected = selectedPeriod == BudgetPeriod.MONTHLY,
                                onClick = { selectedPeriod = BudgetPeriod.MONTHLY },
                                label = { Text("Месяц") }
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            currentUser?.let { user ->
                                selectedPeriod?.let { period ->
                                    val amountValue = amount.toDoubleOrNull() ?: 0.0
                                    if (amountValue > 0) {
                                        budgetViewModel.addBudget(
                                            userId = user.id,
                                            categoryId = null,
                                            amount = amountValue,
                                            period = period
                                        )
                                        showAddDialog = false
                                        amount = ""
                                        selectedPeriod = null
                                    }
                                }
                            }
                        },
                        enabled = amount.toDoubleOrNull() != null && selectedPeriod != null
                    ) {
                        Text("Добавить")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("Отмена")
                    }
                }
            )
        }
    }
}

@Composable
fun BudgetItem(
    budget: com.finanse.mdk.data.model.Budget,
    onDelete: () -> Unit
) {
    val formatter = NumberFormat.getCurrencyInstance(Locale("ru", "RU"))
    
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = formatter.format(budget.amount),
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = if (budget.period == BudgetPeriod.WEEKLY) "Неделя" else "Месяц",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Удалить")
            }
        }
    }
}

