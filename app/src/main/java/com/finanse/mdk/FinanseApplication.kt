package com.finanse.mdk

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FinanseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Инициализация категорий будет выполнена при первом обращении к репозиторию
    }
}
