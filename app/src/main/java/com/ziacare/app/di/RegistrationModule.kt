package com.ziacare.app.di

import com.ziacare.app.BuildConfig
import com.ziacare.app.data.repositories.AppRetrofitService
import com.ziacare.app.data.repositories.registration.RegistrationRemoteDataSource
import com.ziacare.app.data.repositories.registration.RegistrationRepository
import com.ziacare.app.data.repositories.registration.RegistrationService
import com.ziacare.app.security.AuthEncryptedDataManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class RegistrationModule {

    @Provides
    fun providesRegistrationService(): RegistrationService {
        return AppRetrofitService.Builder().build(
            BuildConfig.BASE_URL,
            RegistrationService::class.java
        )
    }

    @Provides
    fun providesRegistrationRemoteDataSource(registerServices: RegistrationService): RegistrationRemoteDataSource {
        return RegistrationRemoteDataSource(registerServices)
    }

    @Provides
    fun providesRegistrationRepository(
        registrationRemoteDataSource: RegistrationRemoteDataSource,
        authEncryptedDataManager: AuthEncryptedDataManager
    ): RegistrationRepository {
        return RegistrationRepository(registrationRemoteDataSource, authEncryptedDataManager)
    }
}