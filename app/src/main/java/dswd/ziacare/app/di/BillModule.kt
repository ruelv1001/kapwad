package dswd.ziacare.app.di

import dswd.ziacare.app.BuildConfig
import dswd.ziacare.app.data.repositories.AppRetrofitService
import dswd.ziacare.app.data.repositories.billing.AllBillListPagingSource
import dswd.ziacare.app.data.repositories.billing.BillRemoteDataSource
import dswd.ziacare.app.data.repositories.billing.BillRepository
import dswd.ziacare.app.data.repositories.billing.BillService
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