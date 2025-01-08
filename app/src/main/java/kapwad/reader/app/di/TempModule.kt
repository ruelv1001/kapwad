package kapwad.reader.app.di


import kapwad.reader.app.BuildConfig
import kapwad.reader.app.data.local.BoilerPlateDatabase
import kapwad.reader.app.data.local.OrderDao
import kapwad.reader.app.data.repositories.AppRetrofitService
import kapwad.reader.app.data.repositories.ph_market.OrderLocalDataSource
import kapwad.reader.app.data.repositories.ph_market.OrderRemoteDataSource
import kapwad.reader.app.data.repositories.ph_market.OrderRepository
import kapwad.reader.app.data.repositories.ph_market.OrderService
import kapwad.reader.app.security.SharedPref
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import kapwad.reader.app.data.local.BillingDao
import kapwad.reader.app.data.local.ConsumerDao
import kapwad.reader.app.data.local.TempDao
import kapwad.reader.app.data.repositories.bill.BillingLocalDataSource
import kapwad.reader.app.data.repositories.bill.BillingRemoteDataSource

import kapwad.reader.app.data.repositories.bill.BillingRepository
import kapwad.reader.app.data.repositories.bill.BillingService
import kapwad.reader.app.data.repositories.consumers.ConsumerLocalDataSource
import kapwad.reader.app.data.repositories.consumers.ConsumerRemoteDataSource
import kapwad.reader.app.data.repositories.consumers.ConsumerRepository
import kapwad.reader.app.data.repositories.consumers.ConsumerService
import kapwad.reader.app.data.repositories.temp.TempLocalDataSource
import kapwad.reader.app.data.repositories.temp.TempRemoteDataSource
import kapwad.reader.app.data.repositories.temp.TempRepository
import kapwad.reader.app.data.repositories.temp.TempService

@Module
@InstallIn(ViewModelComponent::class)
class TempModule {
    
    @Provides
    fun providesTempService(): TempService {
        return AppRetrofitService.Builder().build(
            SharedPref().getLocalUrl().orEmpty().ifEmpty { BuildConfig.BASE_URL },
            TempService::class.java
        )
    }

    @Provides
    fun providesTempDao(db: BoilerPlateDatabase): TempDao {
        return db.tempDao
    }

    @Provides
    fun providesTempLocalDataSource(tempDao: TempDao): TempLocalDataSource {
        return TempLocalDataSource(tempDao)
    }
    
    @Provides
    fun providesTempRemoteDataSource(tempService: TempService): TempRemoteDataSource {
        return TempRemoteDataSource(tempService)
    }

    @Provides
    fun providesTempRepository(
        authRemoteDataSource: TempRemoteDataSource,
        tempLocalDataSource:TempLocalDataSource,
    ): TempRepository {
        return TempRepository(authRemoteDataSource, tempLocalDataSource)
    }
}