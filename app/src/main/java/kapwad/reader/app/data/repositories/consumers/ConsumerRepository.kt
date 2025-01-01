package kapwad.reader.app.data.repositories.bill

import kapwad.reader.app.data.local.BillingDao
import kapwad.reader.app.data.model.CreatedBillListModelData
import kapwad.reader.app.data.model.ProductOrderListModelData

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class BillingRepository @Inject constructor(
    private val billingRemoteDataSource: BillingRemoteDataSource,
    private val billingLocalDataSource: BillingLocalDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {



    fun createBilling(order: CreatedBillListModelData): Flow<CreatedBillListModelData> {
        return flow {
            billingLocalDataSource.createBilling(order)
            emit(order)
        }.flowOn(ioDispatcher)
    }

    fun insertBilling(order: CreatedBillListModelData): Flow<CreatedBillListModelData> {
        return flow {
            billingLocalDataSource.insertBilling(order)
            emit(order)
        }.flowOn(ioDispatcher)
    }



    fun getBilling(): Flow<List<CreatedBillListModelData>> {
        return flow {
            val response = billingLocalDataSource.getBilling() // This returns List<ProductOrderListModelData>
            emit(response)
        }.flowOn(ioDispatcher)
    }



//    fun getTotals(): Flow<Double> {
//        return flow {
//            val response = billingLocalDataSource.getTotal() ?: 0.0 // Handle null case
//            emit(response)
//        }.flowOn(ioDispatcher)
//    }

    fun deleteAllBilling(): Flow<Unit> {
        return flow {
            billingLocalDataSource.deleteAll() // Call the delete function
            emit(Unit) // Emit a Unit value to indicate completion
        }.flowOn(ioDispatcher) // Switch to the IO dispatcher for the operation
    }



//    suspend fun getAllUsers(): List<ProductOrderListModelData>{
//        return billingLocalDataSource.getAllUsers()
//    }

}