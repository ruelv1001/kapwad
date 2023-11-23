package com.ziacare.app.di

import com.ziacare.app.BuildConfig
import com.ziacare.app.data.repositories.AppRetrofitService
import com.ziacare.app.data.repositories.billing.AllBillListPagingSource
import com.ziacare.app.data.repositories.billing.BillRemoteDataSource
import com.ziacare.app.data.repositories.billing.BillRepository
import com.ziacare.app.data.repositories.billing.BillService
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