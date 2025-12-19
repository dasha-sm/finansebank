package com.finanse.mdk.di

import android.content.Context
import androidx.room.Room
import com.finanse.mdk.data.local.FinanseDatabase
import com.finanse.mdk.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FinanseDatabase {
        return FinanseDatabase.getDatabase(context)
    }
    
    @Provides
    fun provideUserDao(database: FinanseDatabase): UserDao {
        return database.userDao()
    }
    
    @Provides
    fun provideCategoryDao(database: FinanseDatabase): CategoryDao {
        return database.categoryDao()
    }
    
    @Provides
    fun provideTransactionDao(database: FinanseDatabase): TransactionDao {
        return database.transactionDao()
    }
    
    @Provides
    fun provideBudgetDao(database: FinanseDatabase): BudgetDao {
        return database.budgetDao()
    }
    
    @Provides
    fun provideFinancialGoalDao(database: FinanseDatabase): com.finanse.mdk.data.local.dao.FinancialGoalDao {
        return database.financialGoalDao()
    }
}


