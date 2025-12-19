package com.finanse.mdk.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.Exclude

@Entity(tableName = "users")
@IgnoreExtraProperties // Игнорирует лишние поля из Firestore
data class User(
    @PrimaryKey
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val role: UserRole = UserRole.USER,
    val createdAt: Long = System.currentTimeMillis(),
    val isBlocked: Boolean = false,
    @Exclude // Исключает поле из Firestore (только для локального хранения)
    val pinCode: String? = null,
    @Exclude // Исключает поле из Firestore (только для локального хранения)
    val biometricEnabled: Boolean = false
) {
    // ОБЯЗАТЕЛЬНЫЙ конструктор для Firebase
    constructor() : this("", "", "", UserRole.USER, 0L, false, null, false)
}

enum class UserRole {
    USER,
    ADMIN
}
