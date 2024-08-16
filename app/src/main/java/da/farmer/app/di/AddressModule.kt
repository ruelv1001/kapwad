package da.farmer.app.di

import da.farmer.app.BuildConfig
import da.farmer.app.data.repositories.AppRetrofitService
import da.farmer.app.data.repositories.address.AddressRemoteDataSource
import da.farmer.app.data.repositories.address.AddressRepository
import da.farmer.app.data.repositories.address.AddressService
import da.farmer.app.security.AuthEncryptedDataManager
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