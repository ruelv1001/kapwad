package kapwad.reader.app.data.repositories.consumers

import kapwad.reader.app.data.model.ConsumerListModelData
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

class ConsumerRepository @Inject constructor(
    private val consumerRemoteDataSource: ConsumerRemoteDataSource,
    private val consumerLocalDataSource: ConsumerLocalDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    fun createConsumerOffline(order: ConsumerListModelData): Flow<ConsumerListModelData> {
        return flow {
            consumerLocalDataSource.insertConsumer(order)
            emit(order)
        }.flowOn(ioDispatcher) // Make sure you have ioDispatcher defined
    }

    fun getAllConsumer(): Flow<List<ConsumerListModelData>> {
        return flow {
            val response = consumerRemoteDataSource.getConsumerList()
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun createConsumer(order: ConsumerListModelData): Flow<ConsumerListModelData> {
        return flow {
            consumerLocalDataSource.createConsumer(order)
            emit(order)
        }.flowOn(ioDispatcher)
    }

    fun insertConsumer(order: ConsumerListModelData): Flow<ConsumerListModelData> {
        return flow {
            consumerLocalDataSource.insertConsumer(order)
            emit(order)
        }.flowOn(ioDispatcher)
    }



    fun getConsumer(): Flow<List<ConsumerListModelData>> {
        return flow {
            val response = consumerLocalDataSource.getConsumer() // This returns List<ProductOrderListModelData>
            emit(response)
        }.flowOn(ioDispatcher)
    }





    fun deleteAllConsumer(): Flow<Unit> {
        return flow {
            consumerLocalDataSource.deleteAll() // Call the delete function
            emit(Unit) // Emit a Unit value to indicate completion
        }.flowOn(ioDispatcher) // Switch to the IO dispatcher for the operation
    }



}