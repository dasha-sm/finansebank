package com.finanse.mdk.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.finanse.mdk.data.model.TransactionType
import com.finanse.mdk.ui.viewmodel.AuthViewModel
import com.finanse.mdk.ui.viewmodel.TransactionViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    navController: NavController,
    transactionViewModel: TransactionViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val categories by transactionViewModel.categories.collectAsState()
    val uiState by transactionViewModel.uiState.collectAsState()
    
    var amount by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf<TransactionType?>(null) }
    var selectedCategoryId by remember { mutableStateOf<String?>(null) }
    var description by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(System.currentTimeMillis()) }
    
    LaunchedEffect(selectedType) {
        selectedType?.let { type ->
            transactionViewModel.loadCategories(type)
        }
    }
    
    LaunchedEffect(uiState) {
        when (uiState) {
            is com.finanse.mdk.ui.viewmodel.TransactionUiState.Success -> {
                navController.popBackStack()
            }
            else -> {}
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Добавить операцию") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Тип операции
            Text("Тип операции", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedType == TransactionType.INCOME,
                    onClick = { selectedType = TransactionType.INCOME },
                    label = { Text("Доход") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = selectedType == TransactionType.EXPENSE,
                    onClick = { selectedType = TransactionType.EXPENSE },
                    label = { Text("Расход") },
                    modifier = Modifier.weight(1f)
                )
            }
            
            // Сумма
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Сумма") },
                enabled = true,
                readOnly = false,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            // Категория
            if (selectedType != null && categories.isNotEmpty()) {
                Text("Категория", style = MaterialTheme.typography.titleMedium)
                categories.forEach { category ->
                    FilterChip(
                        selected = selectedCategoryId == category.id,
                        onClick = { selectedCategoryId = category.id },
                        label = { Text(category.name) },
                        modifier = Modifier.padding(end = 8.dp, bottom = 8.dp)
                    )
                }
            }
            
            // Описание
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
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Кнопка сохранения
            Button(
                onClick = {
                    currentUser?.let { user ->
                        selectedType?.let { type ->
                            selectedCategoryId?.let { categoryId ->
                                val amountValue = amount.toDoubleOrNull() ?: 0.0
                                if (amountValue > 0) {
                                    transactionViewModel.addTransaction(
                                        userId = user.id,
                                        amount = amountValue,
                                        type = type,
                                        categoryId = categoryId,
                                        date = selectedDate,
                                        description = description
                                    )
                                }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = selectedType != null && 
                         selectedCategoryId != null && 
                         amount.toDoubleOrNull() != null &&
                         uiState !is com.finanse.mdk.ui.viewmodel.TransactionUiState.Loading
            ) {
                if (uiState is com.finanse.mdk.ui.viewmodel.TransactionUiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text("Сохранить")
                }
            }
            
            if (uiState is com.finanse.mdk.ui.viewmodel.TransactionUiState.Error) {
                Text(
                    text = (uiState as com.finanse.mdk.ui.viewmodel.TransactionUiState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

