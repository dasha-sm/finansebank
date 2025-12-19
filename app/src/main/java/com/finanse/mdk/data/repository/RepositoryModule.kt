package com.finanse.mdk.data.repository

import com.finanse.mdk.data.local.dao.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideAuthRepository(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore,
        userDao: UserDao
    ): AuthRepository {
        return AuthRepository(firebaseAuth, firestore, userDao)
    }
    
    @Provides
    @Singleton
    fun provideCategoryRepository(
        categoryDao: CategoryDao,
        firestore: FirebaseFirestore
    ): CategoryRepository {
        return CategoryRepository(categoryDao, firestore)
    }
    
    @Provides
    @Singleton
    fun provideTransactionRepository(
        transactionDao: TransactionDao,
        firestore: FirebaseFirestore
    ): TransactionRepository {
        return TransactionRepository(transactionDao, firestore)
    }
    
    @Provides
    @Singleton
    fun provideBudgetRepository(
        budgetDao: BudgetDao,
        firestore: FirebaseFirestore
    ): BudgetRepository {
        return BudgetRepository(budgetDao, firestore)
    }
    
    @Provides
    @Singleton
    fun provideFinancialGoalRepository(
        financialGoalDao: com.finanse.mdk.data.local.dao.FinancialGoalDao,
        firestore: FirebaseFirestore
    ): com.finanse.mdk.data.repository.FinancialGoalRepository {
        return com.finanse.mdk.data.repository.FinancialGoalRepository(financialGoalDao, firestore)
    }
}


