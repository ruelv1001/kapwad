package kapwad.reader.app.di

import kapwad.reader.app.BuildConfig
import kapwad.reader.app.data.repositories.AppRetrofitService
import kapwad.reader.app.data.repositories.geotagging.GeoTaggingRemoteDataSource
import kapwad.reader.app.data.repositories.geotagging.GeoTaggingService
import kapwad.reader.app.data.repositories.geotagging.GeotaggingRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class GeoTaggingModule {

    @Provides
    fun providesWalletService(): GeoTaggingService {
        return AppRetrofitService.Builder().build(
            BuildConfig.BASE_URL,
            GeoTaggingService::class.java
        )
    }

    @Provides
    fun providesGeoTaggingRemoteDataSource(geoTaggingService: GeoTaggingService): GeoTaggingRemoteDataSource {
        return GeoTaggingRemoteDataSource(geoTaggingService)
    }



    @Provides
    fun providesGeoTaggingRepository(
        geoTaggingRemoteDataSource: GeoTaggingRemoteDataSource,
    ): GeotaggingRepository {
        return GeotaggingRepository(geoTaggingRemoteDataSource)
    }

}