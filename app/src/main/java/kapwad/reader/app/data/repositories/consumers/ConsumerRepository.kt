package kapwad.reader.app.data.repositories.consumers

import kapwad.reader.app.data.model.ConsumerListModelData

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


    fun insertConsumers(consumers: List<ConsumerListModelData>): Flow<List<ConsumerListModelData>> {
        return flow {
            consumerLocalDataSource.insertConsumers(consumers)
            emit(consumers)
        }.flowOn(ioDispatcher)
    }






    fun getAllConsumer(): Flow<List<ConsumerListModelData>> {
        return flow {
            val response = consumerRemoteDataSource.getConsumerList()
            emit(response)
        }.flowOn(ioDispatcher)
    }



//    fun insertConsumer(order: ConsumerListModelData): Flow<ConsumerListModelData> {
//        return flow {
//            consumerLocalDataSource.insertConsumer(order)
//            emit(order)
//        }.flowOn(ioDispatcher)
//    }



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



    //Filter ni dre by consumer ID

    fun getConsumerById(id: String): Flow<ConsumerListModelData?> {
        return flow {
            val response = consumerLocalDataSource.getConsumerDetailsById(id)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun getConsumerByEachId(id: String): Flow<ConsumerListModelData?> {
        return flow {
            val response = consumerLocalDataSource.getConsumerDetailsByEachId(id)
            emit(response)
        }.flowOn(ioDispatcher)
    }


    fun searchConsumer(searchQuery: String): Flow<List<ConsumerListModelData>> {
        return flow {
            val response = consumerLocalDataSource.searchConsumer(searchQuery)
            emit(response)
        }.flowOn(ioDispatcher)
    }
}