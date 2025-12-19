package com.finanse.mdk.data.local

import androidx.room.TypeConverter
import com.finanse.mdk.data.model.BudgetPeriod
import com.finanse.mdk.data.model.TransactionType
import com.finanse.mdk.data.model.UserRole

class Converters {
    @TypeConverter
    fun fromTransactionType(value: TransactionType): String {
        return value.name
    }
    
    @TypeConverter
    fun toTransactionType(value: String): TransactionType {
        return TransactionType.valueOf(value)
    }
    
    @TypeConverter
    fun fromUserRole(value: UserRole): String {
        return value.name
    }
    
    @TypeConverter
    fun toUserRole(value: String): UserRole {
        return UserRole.valueOf(value)
    }
    
    @TypeConverter
    fun fromBudgetPeriod(value: BudgetPeriod): String {
        return value.name
    }
    
    @TypeConverter
    fun toBudgetPeriod(value: String): BudgetPeriod {
        return BudgetPeriod.valueOf(value)
    }
}





