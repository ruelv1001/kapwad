package da.farmer.app.di

import da.farmer.app.BuildConfig
import da.farmer.app.data.repositories.AppRetrofitService
import da.farmer.app.data.repositories.wallet.GroupWalletPagingSource
import da.farmer.app.data.repositories.wallet.GroupWalletRemoteDataSource
import da.farmer.app.data.repositories.wallet.GroupWalletRepository
import da.farmer.app.data.repositories.wallet.GroupWalletService
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