package com.finanse.mdk.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finanse.mdk.data.model.Budget
import com.finanse.mdk.data.model.BudgetPeriod
import com.finanse.mdk.data.repository.BudgetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val budgetRepository: BudgetRepository
) : ViewModel() {
    
    private val _budgets = MutableStateFlow<List<Budget>>(emptyList())
    val budgets: StateFlow<List<Budget>> = _budgets.asStateFlow()
    
    private val _uiState = MutableStateFlow<BudgetUiState>(BudgetUiState.Initial)
    val uiState: StateFlow<BudgetUiState> = _uiState.asStateFlow()
    
    fun loadBudgets(userId: String) {
        viewModelScope.launch {
            try {
                budgetRepository.getBudgetsByUser(userId).collect {
                    _budgets.value = it
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _budgets.value = emptyList()
            }
        }
    }
    
    fun addBudget(
        userId: String,
        categoryId: String?,
        amount: Double,
        period: BudgetPeriod
    ) {
        viewModelScope.launch {
            _uiState.value = BudgetUiState.Loading
            try {
                val budget = Budget(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    categoryId = categoryId,
                    amount = amount,
                    period = period,
                    startDate = System.currentTimeMillis()
                )
                budgetRepository.insertBudget(budget)
                _uiState.value = BudgetUiState.Success
            } catch (e: Exception) {
                _uiState.value = BudgetUiState.Error(e.message ?: "Ошибка добавления бюджета")
            }
        }
    }
    
    fun deleteBudget(budget: Budget) {
        viewModelScope.launch {
            try {
                budgetRepository.deleteBudget(budget)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun clearState() {
        _uiState.value = BudgetUiState.Initial
    }
}

sealed class BudgetUiState {
    object Initial : BudgetUiState()
    object Loading : BudgetUiState()
    object Success : BudgetUiState()
    data class Error(val message: String) : BudgetUiState()
}


