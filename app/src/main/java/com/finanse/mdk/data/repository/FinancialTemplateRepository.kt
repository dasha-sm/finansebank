package com.finanse.mdk.data.repository

import com.finanse.mdk.data.local.dao.FinancialTemplateDao
import com.finanse.mdk.data.model.FinancialTemplate
import com.finanse.mdk.data.model.TemplateCategory
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FinancialTemplateRepository @Inject constructor(
    private val templateDao: FinancialTemplateDao,
    private val firestore: FirebaseFirestore
) {
    fun getAllActiveTemplates(): Flow<List<FinancialTemplate>> {
        return templateDao.getAllActiveTemplates()
    }
    
    fun getTemplatesByCategory(category: TemplateCategory): Flow<List<FinancialTemplate>> {
        return templateDao.getTemplatesByCategory(category)
    }
    
    fun getPopularTemplates(): Flow<List<FinancialTemplate>> {
        return templateDao.getPopularTemplates()
    }
    
    suspend fun getTemplateById(templateId: String): FinancialTemplate? {
        return templateDao.getTemplateById(templateId)
    }
    
    suspend fun insertTemplate(template: FinancialTemplate) {
        templateDao.insertTemplate(template)
        firestore.collection("financial_templates").document(template.id).set(template).await()
    }
    
    suspend fun updateTemplate(template: FinancialTemplate) {
        templateDao.updateTemplate(template)
        firestore.collection("financial_templates").document(template.id).set(template).await()
    }
    
    suspend fun deleteTemplate(template: FinancialTemplate) {
        templateDao.deleteTemplate(template)
        firestore.collection("financial_templates").document(template.id).delete().await()
    }
    
    suspend fun incrementViews(templateId: String) {
        templateDao.incrementViews(templateId)
        val template = templateDao.getTemplateById(templateId)
        template?.let {
            firestore.collection("financial_templates").document(templateId)
                .update("views", it.views + 1).await()
        }
    }
}

