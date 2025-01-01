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
import kapwad.reader.app.data.repositories.bill.BillingLocalDataSource
import kapwad.reader.app.data.repositories.bill.BillingRemoteDataSource

import kapwad.reader.app.data.repositories.bill.BillingRepository
import kapwad.reader.app.data.repositories.bill.BillingService
import kapwad.reader.app.data.repositories.consumers.ConsumerLocalDataSource
import kapwad.reader.app.data.repositories.consumers.ConsumerRemoteDataSource
import kapwad.reader.app.data.repositories.consumers.ConsumerRepository
import kapwad.reader.app.data.repositories.consumers.ConsumerService

@Module
@InstallIn(ViewModelComponent::class)
class ConsumerModule {
    
    @Provides
    fun providesConsumerService(): ConsumerService {
        return AppRetrofitService.Builder().build(
            SharedPref().getLocalUrl().orEmpty().ifEmpty { BuildConfig.BASE_URL },
            ConsumerService::class.java
        )
    }

    @Provides
    fun providesConsumerDao(db: BoilerPlateDatabase): ConsumerDao {
        return db.consumerDao
    }

    @Provides
    fun providesConsumerLocalDataSource(consumerDao: ConsumerDao): ConsumerLocalDataSource {
        return ConsumerLocalDataSource(consumerDao)
    }
    
    @Provides
    fun providesConsumerRemoteDataSource(consumerService: ConsumerService): ConsumerRemoteDataSource {
        return ConsumerRemoteDataSource(consumerService)
    }

    @Provides
    fun providesConsumerRepository(
        authRemoteDataSource:  ConsumerRemoteDataSource,
        consumerLocalDataSource: ConsumerLocalDataSource,
    ): ConsumerRepository {
        return ConsumerRepository(authRemoteDataSource, consumerLocalDataSource)
    }
}