package kapwad.reader.app.di

import kapwad.reader.app.BuildConfig
import kapwad.reader.app.data.repositories.AppRetrofitService
import kapwad.reader.app.data.repositories.billing.BillRemoteDataSource
import kapwad.reader.app.data.repositories.billing.BillRepository
import kapwad.reader.app.data.repositories.billing.BillService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class BillModule {

    @Provides
    fun providesBillService(): BillService {
        return AppRetrofitService.Builder().build(
            BuildConfig.BASE_URL,
            BillService::class.java
        )
    }

    @Provides
    fun providesBillRemoteDataSource(billService: BillService): BillRemoteDataSource {
        return BillRemoteDataSource(billService)
    }
    @Provides
    fun providesBillRepository(
        billRemoteDataSource: BillRemoteDataSource
    ): BillRepository {
        return BillRepository(billRemoteDataSource)
    }

}