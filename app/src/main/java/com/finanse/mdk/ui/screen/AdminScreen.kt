package com.finanse.mdk.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.finanse.mdk.data.model.TransactionType
import com.finanse.mdk.data.model.UserRole
import com.finanse.mdk.ui.viewmodel.AdminUiState
import com.finanse.mdk.ui.viewmodel.AdminViewModel
import com.finanse.mdk.ui.viewmodel.AuthViewModel
import com.finanse.mdk.ui.viewmodel.CategoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    navController: NavController,
    adminViewModel: AdminViewModel = hiltViewModel(),
    categoryViewModel: CategoryViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val users by adminViewModel.users.collectAsState()
    val systemCategories by adminViewModel.systemCategories.collectAsState()
    val aggregatedStats by adminViewModel.aggregatedStats.collectAsState()
    val uiState by adminViewModel.uiState.collectAsState()
    
    var selectedTab by remember { mutableStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }
    var pendingMessage by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(currentUser) {
        if (currentUser?.role != UserRole.ADMIN) {
            navController.popBackStack()
            return@LaunchedEffect
        }
        currentUser?.id?.let { adminViewModel.setAdminId(it) }
        adminViewModel.loadUsers()
        adminViewModel.loadSystemCategories()
        adminViewModel.loadAggregatedStats()
        pendingMessage = "Админ-панель открыта"
        snackbarHostState.showSnackbar(pendingMessage!!)
        pendingMessage = null
    }
    
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is AdminUiState.Success -> {
                val message = pendingMessage ?: "Операция выполнена успешно"
                snackbarHostState.showSnackbar(message)
                pendingMessage = null
                adminViewModel.clearState()
            }
            is AdminUiState.Error -> {
                snackbarHostState.showSnackbar(
                    message = state.message,
                    duration = SnackbarDuration.Long
                )
                adminViewModel.clearState()
            }
            else -> {}
        }
    }
    
    // Закрываем диалоги при успешной операции
    LaunchedEffect(uiState) {
        if (uiState is AdminUiState.Success) {
            // Диалоги закроются автоматически через обработку в CategoriesTab
        }
    }
    
    if (currentUser?.role != UserRole.ADMIN) {
        return
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Админ-панель") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        snackbarHost = {
            // Snackbar по умолчанию показывается снизу
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Пользователи") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Категории") }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Статистика") }
                )
            }
            
            when (selectedTab) {
                0 -> UsersTab(
                    users = users,
                    adminViewModel = adminViewModel,
                    onActionMessage = { pendingMessage = it }
                )
                1 -> CategoriesTab(
                    categories = systemCategories,
                    adminViewModel = adminViewModel,
                    onActionMessage = { pendingMessage = it }
                )
                2 -> StatisticsTab(stats = aggregatedStats, uiState = uiState)
            }
        }
    }
}

@Composable
fun UsersTab(
    users: List<com.finanse.mdk.data.model.User>,
    adminViewModel: AdminViewModel,
    onActionMessage: (String) -> Unit
) {
    var userToDelete by remember { mutableStateOf<com.finanse.mdk.data.model.User?>(null) }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(users) { user ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = user.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            if (user.isBlocked) {
                                @OptIn(ExperimentalMaterial3Api::class)
                                Badge {
                                    Text("Заблокирован", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                        Text(
                            text = user.email,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (user.role == UserRole.ADMIN) "Администратор" else "Пользователь",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Row {
                        IconButton(
                            onClick = {
                                if (user.isBlocked) {
                                    onActionMessage("Пользователь разблокирован")
                                    adminViewModel.unblockUser(user.id)
                                } else {
                                    onActionMessage("Пользователь заблокирован")
                                    adminViewModel.blockUser(user.id)
                                }
                            }
                        ) {
                            Icon(
                                if (user.isBlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                                contentDescription = if (user.isBlocked) "Разблокировать" else "Заблокировать",
                                tint = if (user.isBlocked) 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    MaterialTheme.colorScheme.error
                            )
                        }
                        IconButton(
                            onClick = {
                                adminViewModel.resetUserPassword(user.email)
                                onActionMessage("Отправлен сброс пароля")
                            }
                        ) {
                            Icon(
                                Icons.Default.LockReset, 
                                contentDescription = "Сбросить пароль"
                            )
                        }
                        IconButton(
                            onClick = { userToDelete = user }
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Удалить пользователя",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
    
    userToDelete?.let { user ->
        AlertDialog(
            onDismissRequest = { userToDelete = null },
            title = { Text("Удалить пользователя") },
            text = { Text("Вы уверены, что хотите удалить пользователя \"${user.name}\"?") },
            confirmButton = {
                Button(
                    onClick = {
                        adminViewModel.deleteUser(user.id)
                        onActionMessage("Пользователь удален")
                        userToDelete = null
                    }
                ) {
                    Text("Удалить")
                }
            },
            dismissButton = {
                TextButton(onClick = { userToDelete = null }) {
                    Text("Отмена")
                }
            }
        )
    }
}

@Composable
fun CategoriesTab(
    categories: List<com.finanse.mdk.data.model.Category>,
    adminViewModel: AdminViewModel,
    onActionMessage: (String) -> Unit
) {
    val uiState by adminViewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<com.finanse.mdk.data.model.Category?>(null) }
    var deletingCategory by remember { mutableStateOf<com.finanse.mdk.data.model.Category?>(null) }
    var categoryName by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf<TransactionType?>(null) }
    
    // Закрываем диалоги при успешной операции
    LaunchedEffect(uiState) {
        if (uiState is AdminUiState.Success) {
            showAddDialog = false
            showEditDialog = false
            editingCategory = null
            categoryName = ""
            selectedType = null
        }
    }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Системные категории",
                style = MaterialTheme.typography.titleLarge
            )
            IconButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Добавить")
            }
        }
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = category.name,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = if (category.type == TransactionType.INCOME) "Доход" else "Расход",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Row {
                            IconButton(
                                onClick = {
                                    editingCategory = category
                                    categoryName = category.name
                                    selectedType = category.type
                                    showEditDialog = true
                                }
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Редактировать")
                            }
                            IconButton(
                                onClick = { deletingCategory = category }
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Удалить")
                            }
                        }
                    }
                }
            }
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
                        adminViewModel.deleteSystemCategory(category)
                        onActionMessage("Категория удалена")
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
            title = { Text("Добавить системную категорию") },
            text = {
                Column {
                    OutlinedTextField(
                        value = categoryName,
                        onValueChange = { categoryName = it },
                        label = { Text("Название") },
                        enabled = true,
                        readOnly = false,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Тип", style = MaterialTheme.typography.titleSmall)
                    Row {
                        @OptIn(ExperimentalMaterial3Api::class)
                        FilterChip(
                            selected = selectedType == TransactionType.INCOME,
                            onClick = { selectedType = TransactionType.INCOME },
                            label = { Text("Доход") }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        @OptIn(ExperimentalMaterial3Api::class)
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
                        selectedType?.let { type ->
                            adminViewModel.addSystemCategory(categoryName, type)
                            onActionMessage("Категория добавлена")
                        }
                    },
                    enabled = categoryName.isNotBlank() && selectedType != null && 
                             uiState !is AdminUiState.Loading
                ) {
                    if (uiState is AdminUiState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else {
                        Text("Добавить")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showAddDialog = false
                    categoryName = ""
                    selectedType = null
                }) {
                    Text("Отмена")
                }
            }
        )
    }
    
    if (showEditDialog && editingCategory != null) {
        AlertDialog(
            onDismissRequest = { 
                showEditDialog = false
                editingCategory = null
                categoryName = ""
                selectedType = null
            },
            title = { Text("Редактировать системную категорию") },
            text = {
                Column {
                    OutlinedTextField(
                        value = categoryName,
                        onValueChange = { categoryName = it },
                        label = { Text("Название") },
                        enabled = true,
                        readOnly = false,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Тип", style = MaterialTheme.typography.titleSmall)
                    Row {
                        @OptIn(ExperimentalMaterial3Api::class)
                        FilterChip(
                            selected = selectedType == TransactionType.INCOME,
                            onClick = { selectedType = TransactionType.INCOME },
                            label = { Text("Доход") }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        @OptIn(ExperimentalMaterial3Api::class)
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
                        selectedType?.let { type ->
                            val updatedCategory = editingCategory!!.copy(
                                name = categoryName,
                                type = type
                            )
                            adminViewModel.updateSystemCategory(updatedCategory)
                            onActionMessage("Категория обновлена")
                        }
                    },
                    enabled = categoryName.isNotBlank() && selectedType != null && 
                             uiState !is AdminUiState.Loading
                ) {
                    if (uiState is AdminUiState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else {
                        Text("Сохранить")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showEditDialog = false
                    editingCategory = null
                    categoryName = ""
                    selectedType = null
                }) {
                    Text("Отмена")
                }
            }
        )
    }
}

@Composable
fun StatisticsTab(
    stats: com.finanse.mdk.ui.viewmodel.AggregatedStats?,
    uiState: com.finanse.mdk.ui.viewmodel.AdminUiState
) {
    when (uiState) {
        is com.finanse.mdk.ui.viewmodel.AdminUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is com.finanse.mdk.ui.viewmodel.AdminUiState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = (uiState as com.finanse.mdk.ui.viewmodel.AdminUiState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        else -> {
            if (stats == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Нет данных")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        StatCard(
                            title = "Всего пользователей",
                            value = stats.totalUsers.toString()
                        )
                    }
                    item {
                        StatCard(
                            title = "Активных пользователей",
                            value = stats.activeUsers.toString()
                        )
                    }
                    item {
                        StatCard(
                            title = "Операций за неделю",
                            value = stats.transactionsThisWeek.toString()
                        )
                    }
                    item {
                        StatCard(
                            title = "Пользователей с >5 операциями",
                            value = "${stats.usersWithMoreThan5Transactions} (${(stats.usersWithMoreThan5Transactions.toDouble() / stats.totalUsers.coerceAtLeast(1) * 100).toInt()}%)"
                        )
                    }
                    item {
                        StatCard(
                            title = "Самая популярная категория",
                            value = stats.mostPopularCategory
                        )
                    }
                    item {
                        StatCard(
                            title = "Среднее операций на пользователя",
                            value = String.format("%.1f", stats.averageTransactionsPerUser)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
