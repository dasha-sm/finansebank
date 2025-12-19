package com.finanse.mdk.data.local.dao

import androidx.room.*
import com.finanse.mdk.data.model.Transaction
import com.finanse.mdk.data.model.TransactionType
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY date DESC")
    fun getTransactionsByUser(userId: String): Flow<List<Transaction>>
    
    @Query("SELECT * FROM transactions WHERE userId = :userId AND type = :type ORDER BY date DESC")
    fun getTransactionsByUserAndType(userId: String, type: TransactionType): Flow<List<Transaction>>
    
    @Query("""
        SELECT * FROM transactions 
        WHERE userId = :userId 
        AND date >= :startDate 
        AND date <= :endDate 
        ORDER BY date DESC
    """)
    fun getTransactionsByDateRange(
        userId: String,
        startDate: Long,
        endDate: Long
    ): Flow<List<Transaction>>
    
    @Query("""
        SELECT SUM(amount) FROM transactions 
        WHERE userId = :userId 
        AND type = :type 
        AND date >= :startDate 
        AND date <= :endDate
    """)
    suspend fun getTotalByTypeAndDateRange(
        userId: String,
        type: TransactionType,
        startDate: Long,
        endDate: Long
    ): Double?
    
    @Query("SELECT * FROM transactions WHERE userId = :userId AND categoryId = :categoryId")
    fun getTransactionsByCategory(userId: String, categoryId: String): Flow<List<Transaction>>
    
    @Query("SELECT * FROM transactions WHERE synced = 0")
    suspend fun getUnsyncedTransactions(): List<Transaction>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)
    
    @Update
    suspend fun updateTransaction(transaction: Transaction)
    
    @Delete
    suspend fun deleteTransaction(transaction: Transaction)
    
    @Query("DELETE FROM transactions WHERE userId = :userId")
    suspend fun deleteAllUserTransactions(userId: String)
}





