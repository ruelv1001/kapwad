package kapwad.reader.app.di

import kapwad.reader.app.BuildConfig
import kapwad.reader.app.data.repositories.AppRetrofitService
import kapwad.reader.app.data.repositories.wallet.WalletPagingSource
import kapwad.reader.app.data.repositories.wallet.WalletRemoteDataSource
import kapwad.reader.app.data.repositories.wallet.WalletRepository
import kapwad.reader.app.data.repositories.wallet.WalletService
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