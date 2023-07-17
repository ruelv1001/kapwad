package com.lionscare.app.di

import android.app.Application
import androidx.room.Room
import com.lionscare.app.security.AuthEncryptedDataManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SingletonModule {

    @Provides
    @Singleton
    fun providesArticleAppDatabase(app : Application) : com.lionscare.app.data.local.BoilerPlateDatabase {
        return Room.databaseBuilder(
            app,
            com.lionscare.app.data.local.BoilerPlateDatabase::class.java,
            "article_db"
        ).fallbackToDestructiveMigration()
            .build()
    }


    @Provides
    fun providesEncryptedDataManager(): AuthEncryptedDataManager {
        return AuthEncryptedDataManager()
    }
}