package da.farmer.app.di

import da.farmer.app.BuildConfig
import da.farmer.app.data.repositories.AppRetrofitService
import da.farmer.app.data.repositories.profile.ProfileRemoteDataSource
import da.farmer.app.data.repositories.profile.ProfileRepository
import da.farmer.app.data.repositories.profile.ProfileService
import da.farmer.app.security.AuthEncryptedDataManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class ProfileModule {

    @Provides
    fun providesProfileService(): ProfileService {
        return AppRetrofitService.Builder().build(
            BuildConfig.BASE_URL,
            ProfileService::class.java
        )
    }

    @Provides
    fun providesProfileRemoteDataSource(profileService: ProfileService): ProfileRemoteDataSource {
        return ProfileRemoteDataSource(profileService)
    }

    @Provides
    fun providesProfileRepository(
        profileRemoteDataSource: ProfileRemoteDataSource,
        encryptedDataManager: AuthEncryptedDataManager
    ): ProfileRepository {
        return ProfileRepository(profileRemoteDataSource, encryptedDataManager)
    }

}