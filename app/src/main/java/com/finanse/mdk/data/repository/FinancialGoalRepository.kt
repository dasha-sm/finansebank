package com.finanse.mdk.data.repository

import com.finanse.mdk.data.local.dao.FinancialGoalDao
import com.finanse.mdk.data.model.FinancialGoal
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FinancialGoalRepository @Inject constructor(
    private val financialGoalDao: FinancialGoalDao,
    private val firestore: FirebaseFirestore
) {
    fun getGoalsByUser(userId: String): Flow<List<FinancialGoal>> {
        return financialGoalDao.getGoalsByUser(userId)
    }
    
    fun getActiveGoalsByUser(userId: String): Flow<List<FinancialGoal>> {
        return financialGoalDao.getActiveGoalsByUser(userId)
    }
    
    suspend fun getGoalById(goalId: String): FinancialGoal? {
        return financialGoalDao.getGoalById(goalId)
    }
    
    suspend fun insertGoal(goal: FinancialGoal) {
        financialGoalDao.insertGoal(goal)
        firestore.collection("financial_goals").document(goal.id).set(goal).await()
    }
    
    suspend fun updateGoal(goal: FinancialGoal) {
        financialGoalDao.updateGoal(goal)
        firestore.collection("financial_goals").document(goal.id).set(goal).await()
    }
    
    suspend fun deleteGoal(goal: FinancialGoal) {
        financialGoalDao.deleteGoal(goal)
        firestore.collection("financial_goals").document(goal.id).delete().await()
    }
    
    suspend fun addAmountToGoal(goalId: String, amount: Double) {
        val goal = financialGoalDao.getGoalById(goalId) ?: return
        val newAmount = goal.currentAmount + amount
        val updatedGoal = goal.copy(
            currentAmount = newAmount,
            isCompleted = newAmount >= goal.targetAmount
        )
        updateGoal(updatedGoal)
    }
}
