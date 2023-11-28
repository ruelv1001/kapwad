package com.ziacare.app.di

import android.app.Application
import androidx.room.Room
import com.ziacare.app.security.AuthEncryptedDataManager
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
    fun providesArticleAppDatabase(app : Application) : com.ziacare.app.data.local.BoilerPlateDatabase {
        return Room.databaseBuilder(
            app,
            com.ziacare.app.data.local.BoilerPlateDatabase::class.java,
            "article_db"
        ).fallbackToDestructiveMigration()
            .build()
    }


    @Provides
    @Singleton
    fun providesEncryptedDataManager(): AuthEncryptedDataManager {
        return AuthEncryptedDataManager()
    }
}