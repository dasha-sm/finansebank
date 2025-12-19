package com.finanse.mdk.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finanse.mdk.data.model.FinancialGoal
import com.finanse.mdk.data.repository.FinancialGoalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class FinancialGoalViewModel @Inject constructor(
    private val financialGoalRepository: FinancialGoalRepository
) : ViewModel() {
    
    private val _goals = MutableStateFlow<List<FinancialGoal>>(emptyList())
    val goals: StateFlow<List<FinancialGoal>> = _goals.asStateFlow()
    
    private val _activeGoals = MutableStateFlow<List<FinancialGoal>>(emptyList())
    val activeGoals: StateFlow<List<FinancialGoal>> = _activeGoals.asStateFlow()
    
    private val _uiState = MutableStateFlow<GoalUiState>(GoalUiState.Initial)
    val uiState: StateFlow<GoalUiState> = _uiState.asStateFlow()
    
    fun loadGoals(userId: String) {
        viewModelScope.launch {
            try {
                financialGoalRepository.getGoalsByUser(userId).collect {
                    _goals.value = it
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _goals.value = emptyList()
            }
        }
    }
    
    fun loadActiveGoals(userId: String) {
        viewModelScope.launch {
            try {
                financialGoalRepository.getActiveGoalsByUser(userId).collect {
                    _activeGoals.value = it
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _activeGoals.value = emptyList()
            }
        }
    }
    
    fun addGoal(
        userId: String,
        name: String,
        targetAmount: Double,
        deadline: Long,
        description: String = ""
    ) {
        viewModelScope.launch {
            _uiState.value = GoalUiState.Loading
            try {
                val goal = FinancialGoal(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    name = name,
                    targetAmount = targetAmount,
                    deadline = deadline,
                    description = description
                )
                financialGoalRepository.insertGoal(goal)
                _uiState.value = GoalUiState.Success
            } catch (e: Exception) {
                _uiState.value = GoalUiState.Error(e.message ?: "Ошибка добавления цели")
            }
        }
    }
    
    fun updateGoal(goal: FinancialGoal) {
        viewModelScope.launch {
            try {
                financialGoalRepository.updateGoal(goal)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun deleteGoal(goal: FinancialGoal) {
        viewModelScope.launch {
            try {
                financialGoalRepository.deleteGoal(goal)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun addAmountToGoal(goalId: String, amount: Double) {
        viewModelScope.launch {
            try {
                financialGoalRepository.addAmountToGoal(goalId, amount)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun clearState() {
        _uiState.value = GoalUiState.Initial
    }
}

sealed class GoalUiState {
    object Initial : GoalUiState()
    object Loading : GoalUiState()
    object Success : GoalUiState()
    data class Error(val message: String) : GoalUiState()
}
