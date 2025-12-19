package com.finanse.mdk.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finanse.mdk.data.model.Category
import com.finanse.mdk.data.model.TransactionType
import com.finanse.mdk.data.model.UserRole
import com.finanse.mdk.data.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()
    
    private val _systemCategories = MutableStateFlow<List<Category>>(emptyList())
    val systemCategories: StateFlow<List<Category>> = _systemCategories.asStateFlow()
    
    private val _uiState = MutableStateFlow<CategoryUiState>(CategoryUiState.Initial)
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()
    
    var currentUserRole: UserRole = UserRole.USER
    
    fun loadCategories() {
        viewModelScope.launch {
            try {
                categoryRepository.getAllCategories().collect {
                    _categories.value = it
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _categories.value = emptyList()
            }
        }
    }
    
    fun loadSystemCategories() {
        viewModelScope.launch {
            try {
                categoryRepository.getSystemCategories().collect {
                    _systemCategories.value = it
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _systemCategories.value = emptyList()
            }
        }
    }
    
    fun addCategory(name: String, type: TransactionType, userId: String) {
        viewModelScope.launch {
            _uiState.value = CategoryUiState.Loading
            try {
                val category = Category(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    type = type,
                    isSystem = false,
                    createdBy = userId
                )
                categoryRepository.insertCategory(category)
                _uiState.value = CategoryUiState.Success
            } catch (e: Exception) {
                _uiState.value = CategoryUiState.Error(e.message ?: "Ошибка добавления категории")
            }
        }
    }
    
    fun updateCategory(category: Category) {
        viewModelScope.launch {
            if (category.isSystem && currentUserRole != UserRole.ADMIN) {
                _uiState.value = CategoryUiState.Error("Только администратор может редактировать системные категории")
                return@launch
            }
            _uiState.value = CategoryUiState.Loading
            try {
                categoryRepository.updateCategory(category)
                _uiState.value = CategoryUiState.Success
            } catch (e: Exception) {
                _uiState.value = CategoryUiState.Error(e.message ?: "Ошибка обновления категории")
            }
        }
    }
    
    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            if (category.isSystem && currentUserRole != UserRole.ADMIN) {
                _uiState.value = CategoryUiState.Error("Только администратор может удалять системные категории")
                return@launch
            }
            if (category.isSystem) {
                _uiState.value = CategoryUiState.Error("Нельзя удалить системную категорию")
                return@launch
            }
            try {
                categoryRepository.deleteCategory(category)
                _uiState.value = CategoryUiState.Success
            } catch (e: Exception) {
                _uiState.value = CategoryUiState.Error(e.message ?: "Ошибка удаления категории")
            }
        }
    }
    
    fun clearState() {
        _uiState.value = CategoryUiState.Initial
    }
}

sealed class CategoryUiState {
    object Initial : CategoryUiState()
    object Loading : CategoryUiState()
    object Success : CategoryUiState()
    data class Error(val message: String) : CategoryUiState()
}


