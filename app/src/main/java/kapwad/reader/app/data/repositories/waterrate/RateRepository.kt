package kapwad.reader.app.data.repositories.temp

import kapwad.reader.app.data.model.ConsumerListModelData
import kapwad.reader.app.data.model.TempListModelData
import kapwad.reader.app.data.model.CreatedBillListModelData

import kapwad.reader.app.data.repositories.bill.BillingLocalDataSource
import kapwad.reader.app.data.repositories.bill.BillingRemoteDataSource
import kapwad.reader.app.data.repositories.crops.response.CropsResponse

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class TempRepository @Inject constructor(
    private val tempRemoteDataSource: TempRemoteDataSource,
    private val tempLocalDataSource: TempLocalDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {



    fun getAllTemp(): Flow<List<TempListModelData>> {
        return flow {
            val response = tempRemoteDataSource.getTempList()
            emit(response)
        }.flowOn(ioDispatcher)
    }


    fun create(consumers: List<TempListModelData>): Flow<List<TempListModelData>> {
        return flow {
            tempLocalDataSource.insertTemp(consumers)
            emit(consumers)
        }.flowOn(ioDispatcher)
    }





    fun getTemp(): Flow<List<TempListModelData>> {
        return flow {
            val response = tempLocalDataSource.getTemp() // This returns List<ProductOrderListModelData>
            emit(response)
        }.flowOn(ioDispatcher)
    }





    fun deleteAllTemp(): Flow<Unit> {
        return flow {
            tempLocalDataSource.deleteAll() // Call the delete function
            emit(Unit) // Emit a Unit value to indicate completion
        }.flowOn(ioDispatcher) // Switch to the IO dispatcher for the operation
    }



}