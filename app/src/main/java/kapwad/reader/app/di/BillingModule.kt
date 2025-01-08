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
import kapwad.reader.app.data.repositories.bill.BillingLocalDataSource
import kapwad.reader.app.data.repositories.bill.BillingRemoteDataSource

import kapwad.reader.app.data.repositories.bill.BillingRepository
import kapwad.reader.app.data.repositories.bill.BillingService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(ViewModelComponent::class)
class BillingModule {
    
    @Provides
    fun providesBillingService(): BillingService {
        return AppRetrofitService.Builder().build(
            SharedPref().getLocalUrl().orEmpty().ifEmpty { BuildConfig.BASE_URL },
            BillingService::class.java
        )
    }

    @Provides
    fun providesBillingDao(db: BoilerPlateDatabase): BillingDao {
        return db.billingDao
    }

    @Provides
    fun providesBillingLocalDataSource(billingDao: BillingDao): BillingLocalDataSource {
        return BillingLocalDataSource(billingDao)
    }
    
    @Provides
    fun providesBillingsRemoteDataSource(billingService: BillingService): BillingRemoteDataSource {
        return BillingRemoteDataSource(billingService)
    }

    @Provides
    fun providesBillingRepository(
        authRemoteDataSource: BillingRemoteDataSource,
        billingLocalDataSource: BillingLocalDataSource,

    ): BillingRepository {
        return BillingRepository(authRemoteDataSource, billingLocalDataSource)
    }
}