package dswd.ziacare.app.di

import dswd.ziacare.app.BuildConfig
import dswd.ziacare.app.data.repositories.AppRetrofitService
import dswd.ziacare.app.data.repositories.generalsetting.GeneralSettingRemoteDataSource
import dswd.ziacare.app.data.repositories.generalsetting.GeneralSettingRepository
import dswd.ziacare.app.data.repositories.generalsetting.GeneralSettingService
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