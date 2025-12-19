package com.finanse.mdk.data.local.dao

import androidx.room.*
import com.finanse.mdk.data.model.FinancialTemplate
import com.finanse.mdk.data.model.TemplateCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface FinancialTemplateDao {
    @Query("SELECT * FROM financial_templates WHERE isActive = 1 ORDER BY createdAt DESC")
    fun getAllActiveTemplates(): Flow<List<FinancialTemplate>>
    
    @Query("SELECT * FROM financial_templates WHERE category = :category AND isActive = 1 ORDER BY createdAt DESC")
    fun getTemplatesByCategory(category: TemplateCategory): Flow<List<FinancialTemplate>>
    
    @Query("SELECT * FROM financial_templates WHERE id = :templateId")
    suspend fun getTemplateById(templateId: String): FinancialTemplate?
    
    @Query("SELECT * FROM financial_templates ORDER BY views DESC LIMIT 10")
    fun getPopularTemplates(): Flow<List<FinancialTemplate>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(template: FinancialTemplate)
    
    @Update
    suspend fun updateTemplate(template: FinancialTemplate)
    
    @Delete
    suspend fun deleteTemplate(template: FinancialTemplate)
    
    @Query("UPDATE financial_templates SET views = views + 1 WHERE id = :templateId")
    suspend fun incrementViews(templateId: String)
}

