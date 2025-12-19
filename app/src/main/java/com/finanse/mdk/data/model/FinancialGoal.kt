package com.finanse.mdk.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "financial_goals")
data class FinancialGoal(
    @PrimaryKey
    val id: String,
    val userId: String,
    val name: String,
    val targetAmount: Double,
    val currentAmount: Double = 0.0,
    val deadline: Long,
    val createdAt: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = false,
    val description: String = ""
)
