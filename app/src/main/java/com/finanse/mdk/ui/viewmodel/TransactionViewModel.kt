package com.finanse.mdk.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finanse.mdk.data.model.Category
import com.finanse.mdk.data.model.Transaction
import com.finanse.mdk.data.model.TransactionType
import com.finanse.mdk.data.repository.CategoryRepository
import com.finanse.mdk.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()
    
    private val _uiState = MutableStateFlow<TransactionUiState>(TransactionUiState.Initial)
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()
    
    fun loadCategories(type: TransactionType) {
        viewModelScope.launch {
            try {
                categoryRepository.getCategoriesByType(type).collect {
                    _categories.value = it
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _categories.value = emptyList()
            }
        }
    }
    
    fun addTransaction(
        userId: String,
        amount: Double,
        type: TransactionType,
        categoryId: String,
        date: Long,
        description: String
    ) {
        viewModelScope.launch {
            _uiState.value = TransactionUiState.Loading
            try {
                val transaction = Transaction(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    amount = amount,
                    type = type,
                    categoryId = categoryId,
                    date = date,
                    description = description
                )
                transactionRepository.insertTransaction(transaction)
                _uiState.value = TransactionUiState.Success
            } catch (e: Exception) {
                _uiState.value = TransactionUiState.Error(e.message ?: "Ошибка добавления операции")
            }
        }
    }
    
    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                transactionRepository.deleteTransaction(transaction)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun clearState() {
        _uiState.value = TransactionUiState.Initial
    }
}

sealed class TransactionUiState {
    object Initial : TransactionUiState()
    object Loading : TransactionUiState()
    object Success : TransactionUiState()
    data class Error(val message: String) : TransactionUiState()
}


