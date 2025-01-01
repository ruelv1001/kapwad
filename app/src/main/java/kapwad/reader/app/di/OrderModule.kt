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

@Module
@InstallIn(ViewModelComponent::class)
class OrderModule {
    
    @Provides
    fun providesOrderService(): OrderService {
        return AppRetrofitService.Builder().build(
            SharedPref().getLocalUrl().orEmpty().ifEmpty { BuildConfig.BASE_URL },
            OrderService::class.java
        )
    }

    @Provides
    fun providesOrderDao(db: BoilerPlateDatabase): OrderDao {
        return db.orderDao
    }

    @Provides
    fun providesOrderLocalDataSource(orderDao: OrderDao): OrderLocalDataSource {
        return OrderLocalDataSource(orderDao)
    }
    
    @Provides
    fun providesOrderRemoteDataSource(authService: OrderService): OrderRemoteDataSource {
        return OrderRemoteDataSource(authService)
    }

    @Provides
    fun providesOrderRepository(
        authRemoteDataSource: OrderRemoteDataSource,
        orderLocalDataSource: OrderLocalDataSource,
    ): OrderRepository {
        return OrderRepository(authRemoteDataSource, orderLocalDataSource)
    }
}