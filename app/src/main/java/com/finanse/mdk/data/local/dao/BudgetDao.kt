package com.finanse.mdk.data.local.dao

import androidx.room.*
import com.finanse.mdk.data.model.Budget
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budgets WHERE userId = :userId")
    fun getBudgetsByUser(userId: String): Flow<List<Budget>>
    
    @Query("SELECT * FROM budgets WHERE userId = :userId AND categoryId = :categoryId")
    suspend fun getBudgetByCategory(userId: String, categoryId: String?): Budget?
    
    @Query("SELECT * FROM budgets WHERE id = :budgetId")
    suspend fun getBudgetById(budgetId: String): Budget?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: Budget)
    
    @Update
    suspend fun updateBudget(budget: Budget)
    
    @Delete
    suspend fun deleteBudget(budget: Budget)
}





