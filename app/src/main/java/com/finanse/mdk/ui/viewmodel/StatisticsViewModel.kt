package com.finanse.mdk.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finanse.mdk.data.model.Transaction
import com.finanse.mdk.data.model.TransactionType
import com.finanse.mdk.data.repository.CategoryRepository
import com.finanse.mdk.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()
    
    private val _totalIncome = MutableStateFlow(0.0)
    val totalIncome: StateFlow<Double> = _totalIncome.asStateFlow()
    
    private val _totalExpense = MutableStateFlow(0.0)
    val totalExpense: StateFlow<Double> = _totalExpense.asStateFlow()
    
    private val _categoryExpenses = MutableStateFlow<Map<String, Double>>(emptyMap())
    val categoryExpenses: StateFlow<Map<String, Double>> = _categoryExpenses.asStateFlow()
    
    private val _categoryNames = MutableStateFlow<Map<String, String>>(emptyMap())
    val categoryNames: StateFlow<Map<String, String>> = _categoryNames.asStateFlow()
    
    fun loadStatistics(userId: String, period: StatisticsPeriod) {
        viewModelScope.launch {
            try {
                val (startDate, endDate) = getDateRange(period)
                
                transactionRepository.getTransactionsByDateRange(userId, startDate, endDate).collect { transactions ->
                    _transactions.value = transactions
                    
                    try {
                        val income = transactionRepository.getTotalByTypeAndDateRange(
                            userId, TransactionType.INCOME, startDate, endDate
                        )
                        val expense = transactionRepository.getTotalByTypeAndDateRange(
                            userId, TransactionType.EXPENSE, startDate, endDate
                        )
                        
                        _totalIncome.value = income
                        _totalExpense.value = expense
                        
                        // Группировка по категориям
                        val categoryMap = mutableMapOf<String, Double>()
                        transactions
                            .filter { it.type == TransactionType.EXPENSE }
                            .forEach { transaction ->
                                categoryMap[transaction.categoryId] = 
                                    (categoryMap[transaction.categoryId] ?: 0.0) + transaction.amount
                            }
                        _categoryExpenses.value = categoryMap
                        
                        // Загружаем названия категорий
                        val namesMap = mutableMapOf<String, String>()
                        categoryMap.keys.forEach { categoryId ->
                            try {
                                val category = categoryRepository.getCategoryById(categoryId)
                                namesMap[categoryId] = category?.name ?: "Неизвестная категория"
                            } catch (e: Exception) {
                                namesMap[categoryId] = "Неизвестная категория"
                            }
                        }
                        _categoryNames.value = namesMap
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _transactions.value = emptyList()
                _categoryExpenses.value = emptyMap()
                _categoryNames.value = emptyMap()
            }
        }
    }
    
    private fun getDateRange(period: StatisticsPeriod): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        val endDate = calendar.timeInMillis
        
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        
        when (period) {
            StatisticsPeriod.WEEK -> {
                calendar.add(Calendar.DAY_OF_WEEK, -calendar.get(Calendar.DAY_OF_WEEK) + 1)
            }
            StatisticsPeriod.MONTH -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
            }
            StatisticsPeriod.YEAR -> {
                calendar.set(Calendar.DAY_OF_YEAR, 1)
            }
        }
        
        val startDate = calendar.timeInMillis
        return Pair(startDate, endDate)
    }
}

enum class StatisticsPeriod {
    WEEK,
    MONTH,
    YEAR
}


