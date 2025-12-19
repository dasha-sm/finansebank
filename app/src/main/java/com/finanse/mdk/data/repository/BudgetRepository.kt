package com.finanse.mdk.data.repository

import com.finanse.mdk.data.local.dao.BudgetDao
import com.finanse.mdk.data.model.Budget
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetRepository @Inject constructor(
    private val budgetDao: BudgetDao,
    private val firestore: FirebaseFirestore
) {
    fun getBudgetsByUser(userId: String): Flow<List<Budget>> {
        return budgetDao.getBudgetsByUser(userId)
    }
    
    suspend fun getBudgetByCategory(userId: String, categoryId: String?): Budget? {
        return budgetDao.getBudgetByCategory(userId, categoryId)
    }
    
    suspend fun insertBudget(budget: Budget) {
        budgetDao.insertBudget(budget)
        firestore.collection("budgets").document(budget.id).set(budget).await()
    }
    
    suspend fun updateBudget(budget: Budget) {
        budgetDao.updateBudget(budget)
        firestore.collection("budgets").document(budget.id).set(budget).await()
    }
    
    suspend fun deleteBudget(budget: Budget) {
        budgetDao.deleteBudget(budget)
        firestore.collection("budgets").document(budget.id).delete().await()
    }
}





