package dswd.ziacare.app.di

import dswd.ziacare.app.BuildConfig
import dswd.ziacare.app.data.repositories.AppRetrofitService
import dswd.ziacare.app.data.repositories.wallet.GroupWalletPagingSource
import dswd.ziacare.app.data.repositories.wallet.GroupWalletRemoteDataSource
import dswd.ziacare.app.data.repositories.wallet.GroupWalletRepository
import dswd.ziacare.app.data.repositories.wallet.GroupWalletService
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