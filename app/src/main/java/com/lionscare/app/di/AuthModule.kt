package com.lionscare.app.di

import com.lionscare.app.BuildConfig
import com.lionscare.app.data.repositories.auth.AuthLocalDataSource
import com.lionscare.app.data.repositories.auth.AuthRemoteDataSource
import com.lionscare.app.data.repositories.auth.AuthRepository
import com.lionscare.app.data.repositories.auth.AuthService
import com.lionscare.app.security.AuthEncryptedDataManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class AuthModule {

    @Provides
    fun providesAuthService(): AuthService {
        return com.lionscare.app.data.repositories.AppRetrofitService.Builder().build(
            BuildConfig.BASE_URL,
            AuthService::class.java
        )
    }

    @Provides
    fun providesUserDao(db: com.lionscare.app.data.local.BoilerPlateDatabase): com.lionscare.app.data.local.UserDao {
        return db.userDao
    }

    @Provides
    fun providesAuthRemoteDataSource(authService: AuthService): AuthRemoteDataSource {
        return AuthRemoteDataSource(authService)
    }

    @Provides
    fun providesAuthLocalDataSource(userDao: com.lionscare.app.data.local.UserDao): AuthLocalDataSource {
        return AuthLocalDataSource(userDao)
    }

    @Provides
    fun providesAuthRepository(
        authRemoteDataSource: AuthRemoteDataSource,
        encryptedDataManager: AuthEncryptedDataManager,
        authLocalDataSource: AuthLocalDataSource
    ): AuthRepository {
        return AuthRepository(authRemoteDataSource, encryptedDataManager, authLocalDataSource)
    }

}