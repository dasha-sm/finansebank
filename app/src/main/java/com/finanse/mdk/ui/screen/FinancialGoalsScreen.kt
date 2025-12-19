package com.finanse.mdk.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.*
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.finanse.mdk.ui.components.BottomNavigationBar
import com.finanse.mdk.ui.navigation.Screen
import com.finanse.mdk.ui.viewmodel.AuthViewModel
import com.finanse.mdk.ui.viewmodel.FinancialGoalViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialGoalsScreen(
    navController: NavController,
    goalViewModel: FinancialGoalViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val goals by goalViewModel.goals.collectAsState()
    val uiState by goalViewModel.uiState.collectAsState()
    
    var showAddDialog by remember { mutableStateOf(false) }
    var goalName by remember { mutableStateOf("") }
    var targetAmount by remember { mutableStateOf("") }
    var deadline by remember { mutableStateOf(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000) }
    var showDeadlinePicker by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf("") }
    
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            goalViewModel.loadGoals(user.id)
        }
    }
    
    LaunchedEffect(uiState) {
        when (uiState) {
            is com.finanse.mdk.ui.viewmodel.GoalUiState.Success -> {
                showAddDialog = false
                goalName = ""
                targetAmount = ""
                description = ""
                goalViewModel.clearState()
            }
            else -> {}
        }
    }
    
    val formatter = NumberFormat.getCurrencyInstance(Locale("ru", "RU"))
    val dateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Финансовые цели") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Добавить цель")
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController, currentRoute = Screen.Goals.route)
        }
    ) { padding ->
        if (goals.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        Icons.Default.Flag,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Нет финансовых целей",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Создайте свою первую цель",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(goals) { goal ->
                    GoalItem(
                        goal = goal,
                        formatter = formatter,
                        dateFormatter = dateFormatter,
                        onDelete = { goalViewModel.deleteGoal(goal) },
                        onAddAmount = { amount ->
                            goalViewModel.addAmountToGoal(goal.id, amount)
                        }
                    )
                }
            }
        }
        
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Добавить финансовую цель") },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = goalName,
                            onValueChange = { goalName = it },
                            label = { Text("Название цели") },
                            enabled = true,
                            readOnly = false,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = targetAmount,
                            onValueChange = { targetAmount = it },
                            label = { Text("Целевая сумма") },
                            enabled = true,
                            readOnly = false,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Описание") },
                            enabled = true,
                            readOnly = false,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Срок: ${dateFormatter.format(Date(deadline))}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            TextButton(onClick = { showDeadlinePicker = true }) {
                                Text("Выбрать дату")
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            currentUser?.let { user ->
                                val amount = targetAmount.toDoubleOrNull() ?: 0.0
                                if (amount > 0 && goalName.isNotBlank()) {
                                    goalViewModel.addGoal(
                                        userId = user.id,
                                        name = goalName,
                                        targetAmount = amount,
                                        deadline = deadline,
                                        description = description
                                    )
                                }
                            }
                        },
                        enabled = goalName.isNotBlank() && 
                                 targetAmount.toDoubleOrNull() != null &&
                                 uiState !is com.finanse.mdk.ui.viewmodel.GoalUiState.Loading
                    ) {
                        if (uiState is com.finanse.mdk.ui.viewmodel.GoalUiState.Loading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        } else {
                            Text("Добавить")
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("Отмена")
                    }
                }
            )
        }

        if (showDeadlinePicker) {
            val datePickerState = rememberDatePickerState(initialSelectedDateMillis = deadline)
            DatePickerDialog(
                onDismissRequest = { showDeadlinePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val selected = datePickerState.selectedDateMillis
                            if (selected != null) {
                                deadline = selected
                            }
                            showDeadlinePicker = false
                        }
                    ) {
                        Text("Готово")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeadlinePicker = false }) {
                        Text("Отмена")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalItem(
    goal: com.finanse.mdk.data.model.FinancialGoal,
    formatter: NumberFormat,
    dateFormatter: SimpleDateFormat,
    onDelete: () -> Unit,
    onAddAmount: (Double) -> Unit
) {
    val progress = (goal.currentAmount / goal.targetAmount).coerceIn(0.0, 1.0)
    val isCompleted = goal.isCompleted || progress >= 1.0
    var showAddAmountDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var amountToAdd by remember { mutableStateOf("") }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = goal.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    if (goal.description.isNotBlank()) {
                        Text(
                            text = goal.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (isCompleted) {
                    Badge {
                        Text("Завершено")
                    }
                }
            }
            
            LinearProgressIndicator(
                progress = progress.toFloat(),
                modifier = Modifier.fillMaxWidth()
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Текущая сумма",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatter.format(goal.currentAmount),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Целевая сумма",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatter.format(goal.targetAmount),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Text(
                text = "Срок: ${dateFormatter.format(Date(goal.deadline))}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = { showDeleteConfirmDialog = true }) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Удалить")
                }
                if (!isCompleted) {
                    TextButton(
                        onClick = {
                            showAddAmountDialog = true
                        }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Пополнить")
                    }
                }
            }
        }
    }
    
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Удалить цель") },
            text = { Text("Вы уверены, что хотите удалить цель \"${goal.name}\"?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteConfirmDialog = false
                    }
                ) {
                    Text("Удалить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
    
    if (showAddAmountDialog) {
        AlertDialog(
            onDismissRequest = { showAddAmountDialog = false },
            title = { Text("Пополнить цель") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Добавить сумму к цели \"${goal.name}\"",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    OutlinedTextField(
                        value = amountToAdd,
                        onValueChange = { newValue ->
                            if (newValue.all { it.isDigit() || it == '.' || it == ',' }) {
                                amountToAdd = newValue.replace(',', '.')
                            }
                        },
                        label = { Text("Сумма") },
                        placeholder = { Text("0.00") },
                        enabled = true,
                        readOnly = false,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    val remaining = goal.targetAmount - goal.currentAmount
                    if (remaining > 0) {
                        Text(
                            text = "Осталось до цели: ${formatter.format(remaining)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = amountToAdd.toDoubleOrNull()
                        if (amount != null && amount > 0) {
                            onAddAmount(amount)
                            showAddAmountDialog = false
                            amountToAdd = ""
                        }
                    },
                    enabled = amountToAdd.toDoubleOrNull() != null && 
                             amountToAdd.toDoubleOrNull()!! > 0
                ) {
                    Text("Добавить")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showAddAmountDialog = false
                    amountToAdd = ""
                }) {
                    Text("Отмена")
                }
            }
        )
    }
}
