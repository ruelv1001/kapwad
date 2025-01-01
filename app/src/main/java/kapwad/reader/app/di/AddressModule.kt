package kapwad.reader.app.di

import kapwad.reader.app.BuildConfig
import kapwad.reader.app.data.repositories.AppRetrofitService
import kapwad.reader.app.data.repositories.address.AddressRemoteDataSource
import kapwad.reader.app.data.repositories.address.AddressRepository
import kapwad.reader.app.data.repositories.address.AddressService
import kapwad.reader.app.security.AuthEncryptedDataManager
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