package dswd.ziacare.app.di

import dswd.ziacare.app.BuildConfig
import dswd.ziacare.app.data.repositories.AppRetrofitService
import dswd.ziacare.app.data.repositories.address.AddressRemoteDataSource
import dswd.ziacare.app.data.repositories.address.AddressRepository
import dswd.ziacare.app.data.repositories.address.AddressService
import dswd.ziacare.app.security.AuthEncryptedDataManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class AddressModule {

    @Provides
    fun providesAddressService(): AddressService {
        return AppRetrofitService.Builder().build(
            BuildConfig.BASE_URL,
            AddressService::class.java
        )
    }

    @Provides
    fun providesAddressRemoteDataSource(addressService: AddressService ): AddressRemoteDataSource {
        return AddressRemoteDataSource(addressService  )
    }

    @Provides
    fun providesAddressRepository(
        addressRemoteDataSource: AddressRemoteDataSource,
        encryptedDataManager: AuthEncryptedDataManager
    ): AddressRepository {
        return AddressRepository(addressRemoteDataSource, encryptedDataManager)
    }


}