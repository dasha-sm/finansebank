package com.finanse.mdk.data.repository

import com.finanse.mdk.data.local.dao.CategoryDao
import com.finanse.mdk.data.model.Category
import com.finanse.mdk.data.model.TransactionType
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(
    private val categoryDao: CategoryDao,
    private val firestore: FirebaseFirestore
) {
    fun getCategoriesByType(type: TransactionType): Flow<List<Category>> {
        return categoryDao.getCategoriesByType(type)
    }
    
    fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories()
    }
    
    fun getSystemCategories(): Flow<List<Category>> {
        return categoryDao.getSystemCategories()
    }
    
    suspend fun getCategoryById(categoryId: String): Category? {
        return categoryDao.getCategoryById(categoryId)
    }
    
    suspend fun insertCategory(category: Category) {
        categoryDao.insertCategory(category)
        // Синхронизация с Firestore
        firestore.collection("categories").document(category.id).set(category).await()
    }
    
    suspend fun updateCategory(category: Category) {
        if (category.isSystem) {
            // Только админы могут обновлять системные категории
            categoryDao.updateCategory(category)
            firestore.collection("categories").document(category.id).set(category).await()
        } else {
            categoryDao.updateCategory(category)
            firestore.collection("categories").document(category.id).set(category).await()
        }
    }
    
    suspend fun deleteCategory(category: Category) {
        if (!category.isSystem) {
            categoryDao.deleteCategory(category)
            firestore.collection("categories").document(category.id).delete().await()
        }
    }
    
    suspend fun initializeDefaultCategories() {
        // Проверяем, есть ли уже категории
        val existingCategories = categoryDao.getAllCategories().first()
        
        if (existingCategories.isEmpty()) {
            val defaultCategories = listOf(
                // Доходы
                Category(
                    id = UUID.randomUUID().toString(),
                    name = "Зарплата",
                    type = TransactionType.INCOME,
                    isSystem = true,
                    isDefault = true
                ),
                Category(
                    id = UUID.randomUUID().toString(),
                    name = "Подарки",
                    type = TransactionType.INCOME,
                    isSystem = true,
                    isDefault = true
                ),
                Category(
                    id = UUID.randomUUID().toString(),
                    name = "Инвестиции",
                    type = TransactionType.INCOME,
                    isSystem = true,
                    isDefault = true
                ),
                // Расходы
                Category(
                    id = UUID.randomUUID().toString(),
                    name = "Продукты",
                    type = TransactionType.EXPENSE,
                    isSystem = true,
                    isDefault = true
                ),
                Category(
                    id = UUID.randomUUID().toString(),
                    name = "Транспорт",
                    type = TransactionType.EXPENSE,
                    isSystem = true,
                    isDefault = true
                ),
                Category(
                    id = UUID.randomUUID().toString(),
                    name = "Кафе и рестораны",
                    type = TransactionType.EXPENSE,
                    isSystem = true,
                    isDefault = true
                ),
                Category(
                    id = UUID.randomUUID().toString(),
                    name = "Коммунальные услуги",
                    type = TransactionType.EXPENSE,
                    isSystem = true,
                    isDefault = true
                ),
                Category(
                    id = UUID.randomUUID().toString(),
                    name = "Развлечения",
                    type = TransactionType.EXPENSE,
                    isSystem = true,
                    isDefault = true
                ),
                Category(
                    id = UUID.randomUUID().toString(),
                    name = "Здоровье",
                    type = TransactionType.EXPENSE,
                    isSystem = true,
                    isDefault = true
                ),
                Category(
                    id = UUID.randomUUID().toString(),
                    name = "Одежда",
                    type = TransactionType.EXPENSE,
                    isSystem = true,
                    isDefault = true
                )
            )
            categoryDao.insertCategories(defaultCategories)
        }
    }
}

