package com.lionscare.app.di

import com.lionscare.app.BuildConfig
import com.lionscare.app.data.repositories.AppRetrofitService
import com.lionscare.app.data.repositories.billing.AllBillListPagingSource
import com.lionscare.app.data.repositories.billing.BillRemoteDataSource
import com.lionscare.app.data.repositories.billing.BillRepository
import com.lionscare.app.data.repositories.billing.BillService
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