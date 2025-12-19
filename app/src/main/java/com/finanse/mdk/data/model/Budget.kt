package com.finanse.mdk.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "budgets",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["userId"])]
)
data class Budget(
    @PrimaryKey
    val id: String,
    val userId: String,
    val categoryId: String? = null, // null означает общий бюджет
    val amount: Double,
    val period: BudgetPeriod,
    val startDate: Long,
    val createdAt: Long = System.currentTimeMillis()
)

enum class BudgetPeriod {
    WEEKLY,
    MONTHLY
}





