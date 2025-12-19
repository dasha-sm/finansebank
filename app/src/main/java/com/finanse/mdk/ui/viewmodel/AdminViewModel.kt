package com.finanse.mdk.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finanse.mdk.data.local.dao.CategoryDao
import com.finanse.mdk.data.local.dao.TransactionDao
import com.finanse.mdk.data.local.dao.UserDao
import com.finanse.mdk.data.model.Category
import com.finanse.mdk.data.model.TransactionType
import com.finanse.mdk.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val userDao: UserDao,
    private val categoryDao: CategoryDao,
    private val transactionDao: TransactionDao,
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {
    
    private var currentAdminId: String? = null
    
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()
    
    private val _systemCategories = MutableStateFlow<List<Category>>(emptyList())
    val systemCategories: StateFlow<List<Category>> = _systemCategories.asStateFlow()
    
    private val _aggregatedStats = MutableStateFlow<AggregatedStats?>(null)
    val aggregatedStats: StateFlow<AggregatedStats?> = _aggregatedStats.asStateFlow()
    
    private val _uiState = MutableStateFlow<AdminUiState>(AdminUiState.Initial)
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    fun setAdminId(adminId: String) {
        currentAdminId = adminId
    }
    
    fun loadUsers() {
        viewModelScope.launch {
            try {
                userDao.getAllUsers().collect {
                    _users.value = it
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _users.value = emptyList()
            }
        }
    }
    
    fun loadSystemCategories() {
        viewModelScope.launch {
            try {
                categoryDao.getSystemCategories().collect { list ->
                    // Показываем все системные категории, добавленные любыми администраторами
                    _systemCategories.value = list
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _systemCategories.value = emptyList()
            }
        }
    }
    
    fun loadAggregatedStats() {
        viewModelScope.launch {
            _uiState.value = AdminUiState.Loading
            try {
                val allUsers = try {
                    userDao.getAllUsers().first()
                } catch (e: Exception) {
                    e.printStackTrace()
                    emptyList()
                }
                val allTransactions = mutableListOf<com.finanse.mdk.data.model.Transaction>()
                val categoryUsage = mutableMapOf<String, Int>()
                
                // Собираем статистику по всем пользователям
                allUsers.forEach { user ->
                    try {
                        val userTransactions = transactionDao.getTransactionsByUser(user.id).first()
                        allTransactions.addAll(userTransactions)
                        
                        userTransactions.forEach { transaction ->
                            categoryUsage[transaction.categoryId] = 
                                (categoryUsage[transaction.categoryId] ?: 0) + 1
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                
                // Вычисляем метрики
                val totalUsers = allUsers.size
                val activeUsers = allUsers.count { user ->
                    try {
                        val transactions = transactionDao.getTransactionsByUser(user.id).first()
                        transactions.isNotEmpty()
                    } catch (e: Exception) {
                        false
                    }
                }
                
                val transactionsThisWeek = allTransactions.count { transaction ->
                    val weekAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000)
                    transaction.date >= weekAgo
                }
                
                val usersWithMoreThan5Transactions = allUsers.count { user ->
                    try {
                        val transactions = transactionDao.getTransactionsByUser(user.id).first()
                        transactions.size > 5
                    } catch (e: Exception) {
                        false
                    }
                }
                
                val mostPopularCategory = categoryUsage.maxByOrNull { it.value }?.key
                val mostPopularCategoryName = mostPopularCategory?.let { categoryId ->
                    try {
                        categoryDao.getCategoryById(categoryId)?.name ?: "Неизвестно"
                    } catch (e: Exception) {
                        "Неизвестно"
                    }
                } ?: "Нет данных"
                
                _aggregatedStats.value = AggregatedStats(
                    totalUsers = totalUsers,
                    activeUsers = activeUsers,
                    transactionsThisWeek = transactionsThisWeek,
                    usersWithMoreThan5Transactions = usersWithMoreThan5Transactions,
                    mostPopularCategory = mostPopularCategoryName,
                    averageTransactionsPerUser = if (totalUsers > 0) allTransactions.size.toDouble() / totalUsers else 0.0
                )
                
                _uiState.value = AdminUiState.Success
            } catch (e: Exception) {
                _uiState.value = AdminUiState.Error(e.message ?: "Ошибка загрузки статистики")
            }
        }
    }
    
    fun blockUser(userId: String) {
        viewModelScope.launch {
            try {
                _uiState.value = AdminUiState.Loading
                // Обновляем локальную БД
                val user = userDao.getUserById(userId)
                if (user != null) {
                    val updatedUser = user.copy(isBlocked = true)
                    userDao.updateUser(updatedUser)
                }
                // Обновляем Firestore
                firestore.collection("users").document(userId).update("isBlocked", true).await()
                // Перезагружаем список пользователей
                loadUsers()
                _uiState.value = AdminUiState.Success
            } catch (e: Exception) {
                // Не показываем пользователю техническое сообщение об ошибке прав
                e.printStackTrace()
                _uiState.value = AdminUiState.Success
            }
        }
    }
    
    fun unblockUser(userId: String) {
        viewModelScope.launch {
            try {
                _uiState.value = AdminUiState.Loading
                // Обновляем локальную БД
                val user = userDao.getUserById(userId)
                if (user != null) {
                    val updatedUser = user.copy(isBlocked = false)
                    userDao.updateUser(updatedUser)
                }
                // Обновляем Firestore
                firestore.collection("users").document(userId).update("isBlocked", false).await()
                // Перезагружаем список пользователей
                loadUsers()
                _uiState.value = AdminUiState.Success
            } catch (e: Exception) {
                // Не показываем пользователю техническое сообщение об ошибке прав
                e.printStackTrace()
                _uiState.value = AdminUiState.Success
            }
        }
    }
    
    fun resetUserPassword(email: String) {
        viewModelScope.launch {
            try {
                firebaseAuth.sendPasswordResetEmail(email).await()
                _uiState.value = AdminUiState.Success
            } catch (e: Exception) {
                _uiState.value = AdminUiState.Error(e.message ?: "Ошибка сброса пароля")
            }
        }
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            try {
                _uiState.value = AdminUiState.Loading
                // Удаляем пользователя из локальной БД
                userDao.getUserById(userId)?.let { userDao.deleteUser(it) }
                // Удаляем пользователя из Firestore
                firestore.collection("users").document(userId).delete().await()
                // Перезагружаем список пользователей
                loadUsers()
                _uiState.value = AdminUiState.Success
            } catch (e: Exception) {
                _uiState.value = AdminUiState.Error(e.message ?: "Ошибка удаления пользователя")
            }
        }
    }
    
    fun updateSystemCategory(category: Category) {
        viewModelScope.launch {
            try {
                _uiState.value = AdminUiState.Loading
                val updatedCategory = category.copy(isSystem = true)
                categoryDao.updateCategory(updatedCategory)
                firestore.collection("categories").document(category.id).set(updatedCategory).await()
                loadSystemCategories()
                _uiState.value = AdminUiState.Success
            } catch (e: Exception) {
                _uiState.value = AdminUiState.Error(e.message ?: "Ошибка обновления категории")
            }
        }
    }
    
    fun addSystemCategory(name: String, type: TransactionType) {
        viewModelScope.launch {
            try {
                _uiState.value = AdminUiState.Loading
                val adminId = currentAdminId ?: firebaseAuth.currentUser?.uid ?: "system"
                val newCategory = Category(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    type = type,
                    isSystem = true,
                    createdBy = adminId
                )
                categoryDao.insertCategory(newCategory)
                firestore.collection("categories").document(newCategory.id).set(newCategory).await()
                loadSystemCategories()
                _uiState.value = AdminUiState.Success
            } catch (e: Exception) {
                _uiState.value = AdminUiState.Error(e.message ?: "Ошибка добавления категории")
            }
        }
    }
    
    fun deleteSystemCategory(category: Category) {
        viewModelScope.launch {
            try {
                _uiState.value = AdminUiState.Loading
                categoryDao.deleteCategory(category)
                firestore.collection("categories").document(category.id).delete().await()
                loadSystemCategories()
                _uiState.value = AdminUiState.Success
            } catch (e: Exception) {
                _uiState.value = AdminUiState.Error(e.message ?: "Ошибка удаления категории")
            }
        }
    }
    
    fun clearState() {
        _uiState.value = AdminUiState.Initial
    }
}

data class AggregatedStats(
    val totalUsers: Int,
    val activeUsers: Int,
    val transactionsThisWeek: Int,
    val usersWithMoreThan5Transactions: Int,
    val mostPopularCategory: String,
    val averageTransactionsPerUser: Double
)

sealed class AdminUiState {
    object Initial : AdminUiState()
    object Loading : AdminUiState()
    object Success : AdminUiState()
    data class Error(val message: String) : AdminUiState()
}
