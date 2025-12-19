package com.finanse.mdk.security

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PinManager @Inject constructor(
    private val context: Context
) {
    private val prefs: SharedPreferences = 
        context.getSharedPreferences("pin_prefs", Context.MODE_PRIVATE)
    
    private val PIN_KEY = "user_pin"
    
    fun savePin(pin: String) {
        prefs.edit().putString(PIN_KEY, pin).apply()
    }
    
    fun getPin(): String? {
        return prefs.getString(PIN_KEY, null)
    }
    
    fun hasPin(): Boolean {
        return getPin() != null
    }
    
    fun verifyPin(pin: String): Boolean {
        return getPin() == pin
    }
    
    fun clearPin() {
        prefs.edit().remove(PIN_KEY).apply()
    }
}





