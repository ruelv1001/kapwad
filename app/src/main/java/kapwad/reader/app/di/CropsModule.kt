package kapwad.reader.app.di

import kapwad.reader.app.BuildConfig
import kapwad.reader.app.data.repositories.AppRetrofitService
import kapwad.reader.app.data.repositories.crops.CropsPagingSource
import kapwad.reader.app.data.repositories.crops.CropsRemoteDataSource
import kapwad.reader.app.data.repositories.crops.CropsRepository
import kapwad.reader.app.data.repositories.crops.CropsService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class CropsModule {

    @Provides
    fun providesWalletService(): CropsService {
        return AppRetrofitService.Builder().build(
            BuildConfig.BASE_URL,
            CropsService::class.java
        )
    }

    @Provides
    fun providesCropsRemoteDataSource(cropsService: CropsService): CropsRemoteDataSource {
        return CropsRemoteDataSource(cropsService)
    }

    @Provides
    fun providesCropsPagingSource(cropsRemoteDataSource: CropsRemoteDataSource,): CropsPagingSource {
        return CropsPagingSource(cropsRemoteDataSource)
    }

    @Provides
    fun providesCropsRepository(
        cropsRemoteDataSource: CropsRemoteDataSource,
        cropsPagingSource: CropsPagingSource
    ): CropsRepository {
        return CropsRepository(cropsRemoteDataSource, cropsPagingSource)
    }

}