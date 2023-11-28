package com.ziacare.app.di

import com.ziacare.app.BuildConfig
import com.ziacare.app.data.repositories.AppRetrofitService
import com.ziacare.app.data.repositories.profile.ProfileRemoteDataSource
import com.ziacare.app.data.repositories.profile.ProfileRepository
import com.ziacare.app.data.repositories.profile.ProfileService
import com.ziacare.app.security.AuthEncryptedDataManager
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