package com.finanse.mdk.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finanse.mdk.data.model.Transaction
import com.finanse.mdk.data.model.TransactionType
import com.finanse.mdk.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    
    private val _userId = MutableStateFlow<String?>(null)
    
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()
    
    private val _balance = MutableStateFlow(0.0)
    val balance: StateFlow<Double> = _balance.asStateFlow()
    
    private val _totalIncome = MutableStateFlow(0.0)
    val totalIncome: StateFlow<Double> = _totalIncome.asStateFlow()
    
    private val _totalExpense = MutableStateFlow(0.0)
    val totalExpense: StateFlow<Double> = _totalExpense.asStateFlow()
    
    fun setUserId(userId: String) {
        _userId.value = userId
        loadTransactions(userId)
    }
    
    private fun loadTransactions(userId: String) {
        viewModelScope.launch {
            try {
                transactionRepository.getTransactionsByUser(userId).collect { transactions ->
                    _transactions.value = transactions
                    calculateTotals(transactions)
                }
            } catch (e: Exception) {
                // Логируем ошибку, но не крашим приложение
                e.printStackTrace()
                _transactions.value = emptyList()
            }
        }
    }
    
    private fun calculateTotals(transactions: List<Transaction>) {
        var income = 0.0
        var expense = 0.0
        
        transactions.forEach { transaction ->
            when (transaction.type) {
                TransactionType.INCOME -> income += transaction.amount
                TransactionType.EXPENSE -> expense += transaction.amount
            }
        }
        
        _totalIncome.value = income
        _totalExpense.value = expense
        _balance.value = income - expense
    }
    
    fun refresh() {
        _userId.value?.let { userId ->
            viewModelScope.launch {
                try {
                    transactionRepository.syncUnsyncedTransactions()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}


