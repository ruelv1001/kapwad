package com.ziacare.app.di

import com.ziacare.app.BuildConfig
import com.ziacare.app.data.local.BoilerPlateDatabase
import com.ziacare.app.data.local.UserDao
import com.ziacare.app.data.repositories.AppRetrofitService
import com.ziacare.app.data.repositories.auth.AuthLocalDataSource
import com.ziacare.app.data.repositories.auth.AuthRemoteDataSource
import com.ziacare.app.data.repositories.auth.AuthRepository
import com.ziacare.app.data.repositories.auth.AuthService
import com.ziacare.app.security.AuthEncryptedDataManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class AuthModule {

    @Provides
    fun providesAuthService(): AuthService {
        return AppRetrofitService.Builder().build(
            BuildConfig.BASE_URL,
            AuthService::class.java
        )
    }

    @Provides
    fun providesUserDao(db: BoilerPlateDatabase): UserDao {
        return db.userDao
    }

    @Provides
    fun providesAuthRemoteDataSource(authService: AuthService): AuthRemoteDataSource {
        return AuthRemoteDataSource(authService)
    }

    @Provides
    fun providesAuthLocalDataSource(userDao: UserDao): AuthLocalDataSource {
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