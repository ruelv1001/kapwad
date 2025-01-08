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
import kapwad.reader.app.data.local.MeterReaderDao
import kapwad.reader.app.data.local.OtherChargesDao
import kapwad.reader.app.data.local.TempDao
import kapwad.reader.app.data.repositories.bill.BillingLocalDataSource
import kapwad.reader.app.data.repositories.bill.BillingRemoteDataSource

import kapwad.reader.app.data.repositories.bill.BillingRepository
import kapwad.reader.app.data.repositories.bill.BillingService
import kapwad.reader.app.data.repositories.consumers.ConsumerLocalDataSource
import kapwad.reader.app.data.repositories.consumers.ConsumerRemoteDataSource
import kapwad.reader.app.data.repositories.consumers.ConsumerRepository
import kapwad.reader.app.data.repositories.consumers.ConsumerService
import kapwad.reader.app.data.repositories.meter.MeterLocalDataSource
import kapwad.reader.app.data.repositories.meter.MeterRemoteDataSource
import kapwad.reader.app.data.repositories.meter.MeterRepository
import kapwad.reader.app.data.repositories.meter.MeterService
import kapwad.reader.app.data.repositories.others.OtherLocalDataSource
import kapwad.reader.app.data.repositories.others.OtherService
import kapwad.reader.app.data.repositories.others.OthersRemoteDataSource
import kapwad.reader.app.data.repositories.others.OthersRepository
import kapwad.reader.app.data.repositories.temp.TempLocalDataSource
import kapwad.reader.app.data.repositories.temp.TempRemoteDataSource
import kapwad.reader.app.data.repositories.temp.TempRepository
import kapwad.reader.app.data.repositories.temp.TempService

@Module
@InstallIn(ViewModelComponent::class)
class MeterModule {
    
    @Provides
    fun providesMeterService(): MeterService {
        return AppRetrofitService.Builder().build(
            SharedPref().getLocalUrl().orEmpty().ifEmpty { BuildConfig.BASE_URL },
            MeterService::class.java
        )
    }

    @Provides
    fun providesMeterDao(db: BoilerPlateDatabase): MeterReaderDao {
        return db.meterReaderDao
    }

    @Provides
    fun providesMeterLocalDataSource(meterReaderDao: MeterReaderDao): MeterLocalDataSource {
        return MeterLocalDataSource(meterReaderDao)
    }
    
    @Provides
    fun providesMeterRemoteDataSource(meterService: MeterService): MeterRemoteDataSource {
        return MeterRemoteDataSource(meterService)
    }

    @Provides
    fun providesMeterRepository(
        authRemoteDataSource: MeterRemoteDataSource,
        meterLocalDataSource: MeterLocalDataSource,
    ): MeterRepository {
        return MeterRepository(authRemoteDataSource, meterLocalDataSource)
    }
}