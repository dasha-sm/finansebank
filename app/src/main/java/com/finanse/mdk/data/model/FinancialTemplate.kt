package com.finanse.mdk.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "financial_templates")
data class FinancialTemplate(
    @PrimaryKey
    val id: String,
    val title: String,
    val content: String,
    val category: TemplateCategory,
    val createdBy: String? = null, // null = системный шаблон
    val createdAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true,
    val views: Int = 0,
    val likes: Int = 0
)

enum class TemplateCategory {
    SAVING_TIPS, // Советы по накоплению
    INVESTMENT, // Инвестиции
    BUDGETING, // Бюджетирование
    DEBT_MANAGEMENT, // Управление долгами
    GENERAL // Общие советы
}

