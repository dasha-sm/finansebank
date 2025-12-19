package com.finanse.mdk.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey
    val id: String,
    val name: String,
    val type: TransactionType,
    val icon: String = "",
    val isSystem: Boolean = false,
    val createdBy: String? = null,
    val isDefault: Boolean = false
)

enum class TransactionType {
    INCOME,
    EXPENSE
}





