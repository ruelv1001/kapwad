package kapwad.reader.app.data.repositories.ph_market

import kapwad.reader.app.data.model.ProductOrderListModelData

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class OrderRepository @Inject constructor(
    private val orderRemoteDataSource: OrderRemoteDataSource,
    private val orderLocalDataSource: OrderLocalDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {



    fun createOrder(order: ProductOrderListModelData): Flow<ProductOrderListModelData> {
        return flow {
            orderLocalDataSource.createOrder(order)
            emit(order)
        }.flowOn(ioDispatcher)
    }

    fun insertOrder(order: ProductOrderListModelData): Flow<ProductOrderListModelData> {
        return flow {
            orderLocalDataSource.insertOrder(order)
            emit(order)
        }.flowOn(ioDispatcher)
    }



    fun getOrder(): Flow<List<ProductOrderListModelData>> {
        return flow {
            val response = orderLocalDataSource.getOrder() // This returns List<ProductOrderListModelData>
            emit(response)
        }.flowOn(ioDispatcher)
    }




    fun getTotals(): Flow<Double> {
        return flow {
            val response = orderLocalDataSource.getTotal() ?: 0.0 // Handle null case
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun deleteAllOrder(): Flow<Unit> {
        return flow {
            orderLocalDataSource.deleteAll() // Call the delete function
            emit(Unit) // Emit a Unit value to indicate completion
        }.flowOn(ioDispatcher) // Switch to the IO dispatcher for the operation
    }



//    suspend fun getAllUsers(): List<ProductOrderListModelData>{
//        return orderLocalDataSource.getAllUsers()
//    }

}