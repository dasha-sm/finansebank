package com.finanse.mdk.data.local.dao

import androidx.room.*
import com.finanse.mdk.data.model.FinancialGoal
import kotlinx.coroutines.flow.Flow

@Dao
interface FinancialGoalDao {
    @Query("SELECT * FROM financial_goals WHERE userId = :userId ORDER BY deadline ASC")
    fun getGoalsByUser(userId: String): Flow<List<FinancialGoal>>
    
    @Query("SELECT * FROM financial_goals WHERE userId = :userId AND isCompleted = 0")
    fun getActiveGoalsByUser(userId: String): Flow<List<FinancialGoal>>
    
    @Query("SELECT * FROM financial_goals WHERE id = :goalId")
    suspend fun getGoalById(goalId: String): FinancialGoal?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: FinancialGoal)
    
    @Update
    suspend fun updateGoal(goal: FinancialGoal)
    
    @Delete
    suspend fun deleteGoal(goal: FinancialGoal)
    
    @Query("SELECT COUNT(*) FROM financial_goals WHERE userId = :userId")
    suspend fun getGoalCount(userId: String): Int
}
