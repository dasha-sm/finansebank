package com.finanse.mdk.di

import android.content.Context
import com.finanse.mdk.data.repository.*
import com.finanse.mdk.security.PinManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun providePinManager(@ApplicationContext context: Context): PinManager {
        return PinManager(context)
    }
}





