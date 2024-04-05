package dswd.ziacare.app.di

import dswd.ziacare.app.BuildConfig
import dswd.ziacare.app.data.repositories.AppRetrofitService
import dswd.ziacare.app.data.repositories.wallet.WalletPagingSource
import dswd.ziacare.app.data.repositories.wallet.WalletRemoteDataSource
import dswd.ziacare.app.data.repositories.wallet.WalletRepository
import dswd.ziacare.app.data.repositories.wallet.WalletService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class WalletModule {

    @Provides
    fun providesWalletService(): WalletService {
        return AppRetrofitService.Builder().build(
            BuildConfig.BASE_URL,
            WalletService::class.java
        )
    }

    @Provides
    fun providesWalletRemoteDataSource(walletService: WalletService): WalletRemoteDataSource {
        return WalletRemoteDataSource(walletService)
    }

    @Provides
    fun providesWalletPagingSource(walletRemoteDataSource: WalletRemoteDataSource,): WalletPagingSource {
        return WalletPagingSource(walletRemoteDataSource)
    }

    @Provides
    fun providesWalletRepository(
        walletRemoteDataSource: WalletRemoteDataSource,
        walletPagingSource: WalletPagingSource
    ): WalletRepository {
        return WalletRepository(walletRemoteDataSource, walletPagingSource)
    }

}