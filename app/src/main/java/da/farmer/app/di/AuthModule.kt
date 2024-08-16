package da.farmer.app.di

import da.farmer.app.BuildConfig
import da.farmer.app.data.local.BoilerPlateDatabase
import da.farmer.app.data.local.UserDao
import da.farmer.app.data.repositories.AppRetrofitService
import da.farmer.app.data.repositories.auth.AuthLocalDataSource
import da.farmer.app.data.repositories.auth.AuthRemoteDataSource
import da.farmer.app.data.repositories.auth.AuthRepository
import da.farmer.app.data.repositories.auth.AuthService
import da.farmer.app.security.AuthEncryptedDataManager
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