package com.finanse.mdk.data.repository

import com.finanse.mdk.data.local.dao.TransactionDao
import com.finanse.mdk.data.model.Transaction
import com.finanse.mdk.data.model.TransactionType
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao,
    private val firestore: FirebaseFirestore
) {
    fun getTransactionsByUser(userId: String): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByUser(userId)
    }
    
    fun getTransactionsByUserAndType(userId: String, type: TransactionType): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByUserAndType(userId, type)
    }
    
    fun getTransactionsByDateRange(
        userId: String,
        startDate: Long,
        endDate: Long
    ): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByDateRange(userId, startDate, endDate)
    }
    
    suspend fun getTotalByTypeAndDateRange(
        userId: String,
        type: TransactionType,
        startDate: Long,
        endDate: Long
    ): Double {
        return transactionDao.getTotalByTypeAndDateRange(userId, type, startDate, endDate) ?: 0.0
    }
    
    suspend fun insertTransaction(transaction: Transaction) {
        transactionDao.insertTransaction(transaction)
        // Синхронизация с Firestore (асинхронно)
        syncTransactionToFirestore(transaction)
    }
    
    suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.updateTransaction(transaction.copy(synced = false))
        syncTransactionToFirestore(transaction)
    }
    
    suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.deleteTransaction(transaction)
        firestore.collection("transactions").document(transaction.id).delete().await()
    }
    
    private suspend fun syncTransactionToFirestore(transaction: Transaction) {
        try {
            firestore.collection("transactions").document(transaction.id).set(transaction).await()
            transactionDao.updateTransaction(transaction.copy(synced = true))
        } catch (e: Exception) {
            // Ошибка синхронизации - транзакция останется с synced = false
            // Будет синхронизирована позже
        }
    }
    
    suspend fun syncUnsyncedTransactions() {
        val unsynced = transactionDao.getUnsyncedTransactions()
        unsynced.forEach { transaction ->
            syncTransactionToFirestore(transaction)
        }
    }
}





