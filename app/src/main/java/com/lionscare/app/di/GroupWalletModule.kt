package com.lionscare.app.di

import com.lionscare.app.BuildConfig
import com.lionscare.app.data.repositories.AppRetrofitService
import com.lionscare.app.data.repositories.wallet.GroupWalletPagingSource
import com.lionscare.app.data.repositories.wallet.GroupWalletRemoteDataSource
import com.lionscare.app.data.repositories.wallet.GroupWalletRepository
import com.lionscare.app.data.repositories.wallet.GroupWalletService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class GroupWalletModule {

    @Provides
    fun providesGroupWalletService(): GroupWalletService {
        return AppRetrofitService.Builder().build(
            BuildConfig.BASE_URL,
            GroupWalletService::class.java
        )
    }

    @Provides
    fun providesGroupWalletRemoteDataSource(walletService: GroupWalletService): GroupWalletRemoteDataSource {
        return GroupWalletRemoteDataSource(walletService)
    }

    @Provides
    fun providesGroupWalletRepository(
        walletRemoteDataSource: GroupWalletRemoteDataSource,
    ): GroupWalletRepository {
        return GroupWalletRepository(walletRemoteDataSource)
    }

}