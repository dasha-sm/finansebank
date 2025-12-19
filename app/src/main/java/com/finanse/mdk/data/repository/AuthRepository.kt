package com.finanse.mdk.data.repository

import com.finanse.mdk.data.local.dao.UserDao
import com.finanse.mdk.data.model.User
import com.finanse.mdk.data.model.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val userDao: UserDao
) {
    suspend fun register(email: String, password: String, name: String): Result<User> {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: UUID.randomUUID().toString()
            
            val user = User(
                id = userId,
                email = email,
                name = name,
                role = if (email == "admin@test.com") UserRole.ADMIN else UserRole.USER
            )
            
            // Сохраняем в локальную БД
            userDao.insertUser(user)
            
            // Сохраняем в Firestore
            firestore.collection("users").document(userId).set(user).await()
            
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: return Result.failure(Exception("User not found"))
            
            // Получаем из Firestore
            val userDoc = firestore.collection("users").document(userId).get().await()
            val user = userDoc.toObject(User::class.java) ?: run {
                // Если нет в Firestore, создаем из Firebase Auth
                User(
                    id = userId,
                    email = email,
                    name = authResult.user?.displayName ?: "User",
                    role = if (email == "admin@test.com") UserRole.ADMIN else UserRole.USER
                )
            }
            
            // Проверяем, не заблокирован ли пользователь
            if (user.isBlocked) {
                return Result.failure(Exception("Ваш аккаунт заблокирован администратором"))
            }
            
            // Сохраняем в локальную БД
            userDao.insertUser(user)
            
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getCurrentUser(): User? {
        return try {
            val userId = firebaseAuth.currentUser?.uid ?: return null
            userDao.getUserById(userId)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    fun logout() {
        firebaseAuth.signOut()
    }
    
    suspend fun updateUserPin(userId: String, pinCode: String) {
        try {
            val user = userDao.getUserById(userId) ?: return
            val updatedUser = user.copy(pinCode = pinCode)
            userDao.updateUser(updatedUser)
            firestore.collection("users").document(userId).update("pinCode", pinCode).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    suspend fun setBiometricEnabled(userId: String, enabled: Boolean) {
        try {
            val user = userDao.getUserById(userId) ?: return
            val updatedUser = user.copy(biometricEnabled = enabled)
            userDao.updateUser(updatedUser)
            firestore.collection("users").document(userId).update("biometricEnabled", enabled).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}


