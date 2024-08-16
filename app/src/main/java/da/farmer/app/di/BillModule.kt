package da.farmer.app.di

import da.farmer.app.BuildConfig
import da.farmer.app.data.repositories.AppRetrofitService
import da.farmer.app.data.repositories.billing.AllBillListPagingSource
import da.farmer.app.data.repositories.billing.BillRemoteDataSource
import da.farmer.app.data.repositories.billing.BillRepository
import da.farmer.app.data.repositories.billing.BillService
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