package com.finanse.mdk.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["userId"]), Index(value = ["categoryId"])]
)
data class Transaction(
    @PrimaryKey
    val id: String,
    val userId: String,
    val amount: Double,
    val type: TransactionType,
    val categoryId: String,
    val date: Long,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val synced: Boolean = false
)





