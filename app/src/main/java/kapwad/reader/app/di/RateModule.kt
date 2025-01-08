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
import kapwad.reader.app.data.local.RateADao
import kapwad.reader.app.data.local.RateBDao
import kapwad.reader.app.data.local.RateCDao
import kapwad.reader.app.data.local.RateDao
import kapwad.reader.app.data.local.RateReDao
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
import kapwad.reader.app.data.repositories.waterrate.RateLocalDataSource
import kapwad.reader.app.data.repositories.waterrate.RateRemoteDataSource
import kapwad.reader.app.data.repositories.waterrate.RateRepository
import kapwad.reader.app.data.repositories.waterrate.RateService

@Module
@InstallIn(ViewModelComponent::class)
class RateModule {
    
    @Provides
    fun providesRateService(): RateService {
        return AppRetrofitService.Builder().build(
            SharedPref().getLocalUrl().orEmpty().ifEmpty { BuildConfig.BASE_URL },
            RateService::class.java
        )
    }

    @Provides
    fun providesRateDao(db: BoilerPlateDatabase): RateDao {
        return db.rateDao
    }

    @Provides
    fun providesRateADao(db: BoilerPlateDatabase): RateADao {
        return db.rateADao
    }

    @Provides
    fun providesRateBDao(db: BoilerPlateDatabase): RateBDao {
        return db.rateBDao
    }

    @Provides
    fun providesRateCDao(db: BoilerPlateDatabase): RateCDao {
        return db.rateCDao
    }
    @Provides
    fun providesRateReDao(db: BoilerPlateDatabase): RateReDao {
        return db.rateReDao
    }



    @Provides
    fun providesRateLocalDataSource(rateDao: RateDao,rateADao: RateADao,rateBDao: RateBDao,rateCDao: RateCDao,rateReDao: RateReDao): RateLocalDataSource {
        return RateLocalDataSource(rateDao,rateADao,rateBDao,rateCDao,rateReDao)
    }


    
    @Provides
    fun providesRateRemoteDataSource(rateService: RateService): RateRemoteDataSource {
        return RateRemoteDataSource(rateService)
    }

    @Provides
    fun providesRat3Repository(
        authRemoteDataSource: RateRemoteDataSource,
        rateLocalDataSource: RateLocalDataSource,
    ): RateRepository {
        return RateRepository(authRemoteDataSource, rateLocalDataSource)
    }
}