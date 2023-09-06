package com.lionscare.app.di

import com.lionscare.app.BuildConfig
import com.lionscare.app.data.repositories.AppRetrofitService
import com.lionscare.app.data.repositories.assistance.AssistanceRemoteDataSource
import com.lionscare.app.data.repositories.assistance.AssistanceRepository
import com.lionscare.app.data.repositories.assistance.AssistanceService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class AssistanceModule {

    @Provides
    fun providesAssistanceService(): AssistanceService {
        return AppRetrofitService.Builder().build(
            BuildConfig.BASE_URL,
            AssistanceService::class.java
        )
    }

    @Provides
    fun providesAssistanceRemoteDataSource(assistanceService: AssistanceService): AssistanceRemoteDataSource {
        return AssistanceRemoteDataSource(assistanceService)
    }

    @Provides
    fun providesAssistanceRepository(
        assistanceRemoteDataSource: AssistanceRemoteDataSource
    ): AssistanceRepository {
        return AssistanceRepository(assistanceRemoteDataSource)
    }

}