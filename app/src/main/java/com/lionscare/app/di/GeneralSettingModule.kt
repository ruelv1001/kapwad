package com.lionscare.app.di

import com.lionscare.app.BuildConfig
import com.lionscare.app.data.repositories.AppRetrofitService
import com.lionscare.app.data.repositories.generalsetting.GeneralSettingRemoteDataSource
import com.lionscare.app.data.repositories.generalsetting.GeneralSettingRepository
import com.lionscare.app.data.repositories.generalsetting.GeneralSettingService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class GeneralSettingModule {

    @Provides
    fun providesGeneralSettingService(): GeneralSettingService {
        return AppRetrofitService.Builder().build(
            BuildConfig.BASE_URL,
            GeneralSettingService::class.java
        )
    }

    @Provides
    fun providesGeneralSettingRemoteDataSource(generalSettingService: GeneralSettingService): GeneralSettingRemoteDataSource {
        return GeneralSettingRemoteDataSource(generalSettingService)
    }

    @Provides
    fun providesGeneralSettingRepository(
        generalSettingRemoteDataSource: GeneralSettingRemoteDataSource,
    ): GeneralSettingRepository {
        return GeneralSettingRepository(generalSettingRemoteDataSource)
    }
    
}