package com.lionscare.app.di

import com.lionscare.app.BuildConfig
import com.lionscare.app.data.repositories.AppRetrofitService
import com.lionscare.app.data.repositories.address.AddressRemoteDataSource
import com.lionscare.app.data.repositories.address.AddressRepository
import com.lionscare.app.data.repositories.address.AddressService
import com.lionscare.app.security.AuthEncryptedDataManager
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