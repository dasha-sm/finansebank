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
import com.finanse.mdk.data.model.TransactionType
import com.finanse.mdk.ui.components.BottomNavigationBar
import com.finanse.mdk.ui.navigation.Screen
import com.finanse.mdk.ui.viewmodel.AuthViewModel
import com.finanse.mdk.ui.viewmodel.CategoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    navController: NavController,
    categoryViewModel: CategoryViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val categories by categoryViewModel.categories.collectAsState()
    val uiState by categoryViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    var showAddDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf<TransactionType?>(null) }
    var deletingCategory by remember { mutableStateOf<com.finanse.mdk.data.model.Category?>(null) }
    
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            categoryViewModel.currentUserRole = user.role
            categoryViewModel.loadCategories()
        }
    }
    
    LaunchedEffect(uiState) {
        when (uiState) {
            is com.finanse.mdk.ui.viewmodel.CategoryUiState.Success -> {
                snackbarHostState.showSnackbar("Категория успешно удалена")
                categoryViewModel.clearState()
            }
            is com.finanse.mdk.ui.viewmodel.CategoryUiState.Error -> {
                // Не показываем ошибки на английском, только логируем
                categoryViewModel.clearState()
            }
            else -> {}
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Категории") },
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
        },
        bottomBar = {
            BottomNavigationBar(navController = navController, currentRoute = Screen.Categories.route)
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(16.dp)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                CategoryItem(
                    category = category,
                    onDelete = {
                        deletingCategory = category
                    },
                    canDelete = !category.isSystem || 
                               (currentUser?.role == com.finanse.mdk.data.model.UserRole.ADMIN)
                )
            }
        }
        
        deletingCategory?.let { category ->
            AlertDialog(
                onDismissRequest = { deletingCategory = null },
                title = { Text("Удалить категорию") },
                text = { Text("Вы уверены, что хотите удалить категорию \"${category.name}\"?") },
                confirmButton = {
                    Button(
                        onClick = {
                            categoryViewModel.deleteCategory(category)
                            deletingCategory = null
                        }
                    ) {
                        Text("Удалить")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { deletingCategory = null }) {
                        Text("Отмена")
                    }
                }
            )
        }
        
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Добавить категорию") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = newCategoryName,
                            onValueChange = { newCategoryName = it },
                            label = { Text("Название") },
                            enabled = true,
                            readOnly = false,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Тип", style = MaterialTheme.typography.titleSmall)
                        Row {
                            FilterChip(
                                selected = selectedType == TransactionType.INCOME,
                                onClick = { selectedType = TransactionType.INCOME },
                                label = { Text("Доход") }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            FilterChip(
                                selected = selectedType == TransactionType.EXPENSE,
                                onClick = { selectedType = TransactionType.EXPENSE },
                                label = { Text("Расход") }
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            currentUser?.let { user ->
                                selectedType?.let { type ->
                                    categoryViewModel.addCategory(newCategoryName, type, user.id)
                                    showAddDialog = false
                                    newCategoryName = ""
                                    selectedType = null
                                }
                            }
                        },
                        enabled = newCategoryName.isNotBlank() && selectedType != null
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
fun CategoryItem(
    category: com.finanse.mdk.data.model.Category,
    onDelete: () -> Unit,
    canDelete: Boolean
) {
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
                    text = category.name,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = if (category.type == TransactionType.INCOME) "Доход" else "Расход",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (category.isSystem) {
                    Text(
                        text = "Системная",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            if (canDelete && !category.isSystem) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Удалить")
                }
            }
        }
    }
}

