package kapwad.reader.app.di

import kapwad.reader.app.BuildConfig
import kapwad.reader.app.data.repositories.AppRetrofitService
import kapwad.reader.app.data.repositories.wallet.GroupWalletRemoteDataSource
import kapwad.reader.app.data.repositories.wallet.GroupWalletRepository
import kapwad.reader.app.data.repositories.wallet.GroupWalletService
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