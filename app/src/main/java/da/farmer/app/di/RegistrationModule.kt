package da.farmer.app.di

import da.farmer.app.BuildConfig
import da.farmer.app.data.repositories.AppRetrofitService
import da.farmer.app.data.repositories.registration.RegistrationRemoteDataSource
import da.farmer.app.data.repositories.registration.RegistrationRepository
import da.farmer.app.data.repositories.registration.RegistrationService
import da.farmer.app.security.AuthEncryptedDataManager
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