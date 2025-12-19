package com.finanse.mdk.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.finanse.mdk.data.local.dao.*
import com.finanse.mdk.data.model.*

@Database(
    entities = [User::class, Category::class, Transaction::class, Budget::class, FinancialGoal::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FinanseDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
    abstract fun budgetDao(): BudgetDao
    abstract fun financialGoalDao(): FinancialGoalDao
    
    companion object {
        @Volatile
        private var INSTANCE: FinanseDatabase? = null
        private const val DB_NAME = "finanse.db"
        // TODO: Включить шифрование после проверки работы SQLCipher
        // private const val DB_PASSWORD = "default_password_change_in_production"
        
        fun getDatabase(context: Context): FinanseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FinanseDatabase::class.java,
                    DB_NAME
                )
                    // Временно отключено шифрование для проверки компиляции
                    // TODO: Включить после проверки работы SQLCipher
                    // .openHelperFactory(createOpenHelperFactory(DB_PASSWORD))
                    .fallbackToDestructiveMigration() // Для разработки - удаляет старую БД
                    .build()
                INSTANCE = instance
                instance
            }
        }
        
        // Временно отключено для проверки компиляции
        // TODO: Включить после проверки работы SQLCipher
        /*
        private fun createOpenHelperFactory(password: String): androidx.sqlite.db.SupportSQLiteOpenHelper.Factory {
            return net.zetetic.database.sqlcipher.SupportFactory(password.toByteArray())
        }
        */
    }
}
