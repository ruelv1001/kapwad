package kapwad.reader.app.data.repositories.bill

import kapwad.reader.app.data.local.BillingDao
import kapwad.reader.app.data.model.ConsumerListModelData
import kapwad.reader.app.data.model.CreatedBillListModelData
import kapwad.reader.app.data.model.MeterReaderListModelData
import kapwad.reader.app.data.model.ProductOrderListModelData
import kapwad.reader.app.data.repositories.bill.response.BillUploadedResponse
import kapwad.reader.app.data.repositories.crops.request.CropDetailsRequest
import kapwad.reader.app.data.repositories.crops.response.CropDetailsResponse

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class BillingRepository @Inject constructor(
    private val billingRemoteDataSource: BillingRemoteDataSource,
    private val billingLocalDataSource: BillingLocalDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,

) {

    fun uploadJson(upload:String): Flow<BillUploadedResponse> {
        return billingRemoteDataSource.getUpload(upload)
            .flowOn(ioDispatcher)
    }


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


    fun getMeterByCredential(month: String, owners_id: String): Flow<CreatedBillListModelData?> {
        return flow {
            val response = billingLocalDataSource.getValidatedExistingBill(month,owners_id)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun searchConsumer(searchQuery: String): Flow<List<CreatedBillListModelData>> {
        return flow {
            val response = billingLocalDataSource.searchConsumer(searchQuery)
            emit(response)
        }.flowOn(ioDispatcher)
    }
}