package kapwad.reader.app.data.repositories.waterrate

import kapwad.reader.app.data.model.ConsumerListModelData
import kapwad.reader.app.data.model.TempListModelData
import kapwad.reader.app.data.model.CreatedBillListModelData
import kapwad.reader.app.data.model.RateAListModelData
import kapwad.reader.app.data.model.RateBListModelData
import kapwad.reader.app.data.model.RateCListModelData
import kapwad.reader.app.data.model.RateListModelData

import kapwad.reader.app.data.repositories.bill.BillingLocalDataSource
import kapwad.reader.app.data.repositories.bill.BillingRemoteDataSource
import kapwad.reader.app.data.repositories.crops.response.CropsResponse

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class RateRepository @Inject constructor(
    private val rateRemoteDataSource: RateRemoteDataSource,
    private val rateLocalDataSource: RateLocalDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {



    fun getAllWR(): Flow<List<RateListModelData>> {
        return flow {
            val response = rateRemoteDataSource.getRateList()
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun getAllWRA(): Flow<List<RateAListModelData>> {
        return flow {
            val response = rateRemoteDataSource.getRateAList()
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun getAllWRB(): Flow<List<RateBListModelData>> {
        return flow {
            val response = rateRemoteDataSource.getRateBList()
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun getAllWRC(): Flow<List<RateCListModelData>> {
        return flow {
            val response = rateRemoteDataSource.getRateCList()
            emit(response)
        }.flowOn(ioDispatcher)
    }
    //---------------------------------------


    fun create(rate: List<RateListModelData>): Flow<List<RateListModelData>> {
        return flow {
            rateLocalDataSource.insertWR(rate)
            emit(rate)
        }.flowOn(ioDispatcher)
    }

    fun createA(rate: List<RateAListModelData>): Flow<List<RateAListModelData>> {
        return flow {
            rateLocalDataSource.insertWRA(rate)
            emit(rate)
        }.flowOn(ioDispatcher)
    }

    fun createB(rate: List<RateBListModelData>): Flow<List<RateBListModelData>> {
        return flow {
            rateLocalDataSource.insertWRB(rate)
            emit(rate)
        }.flowOn(ioDispatcher)
    }

    fun createC(rate: List<RateCListModelData>): Flow<List<RateCListModelData>> {
        return flow {
            rateLocalDataSource.insertWRC(rate)
            emit(rate)
        }.flowOn(ioDispatcher)
    }

    //-----------------------------------------



    fun getRate(): Flow<List<RateListModelData>> {
        return flow {
            val response = rateLocalDataSource.getWR()
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun getRateA(): Flow<List<RateAListModelData>> {
        return flow {
            val response = rateLocalDataSource.getWRA()
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun getRateB(): Flow<List<RateBListModelData>> {
        return flow {
            val response = rateLocalDataSource.getWRB()
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun getRateC(): Flow<List<RateCListModelData>> {
        return flow {
            val response = rateLocalDataSource.getWRC()
            emit(response)
        }.flowOn(ioDispatcher)
    }

    //-----------------------------------





    fun deleteAllTWR(): Flow<Unit> {
        return flow {
            rateLocalDataSource.deleteAllWR() // Call the delete function
            emit(Unit) // Emit a Unit value to indicate completion
        }.flowOn(ioDispatcher) // Switch to the IO dispatcher for the operation
    }

    fun deleteAllTWRA(): Flow<Unit> {
        return flow {
            rateLocalDataSource.deleteAllWRA() // Call the delete function
            emit(Unit) // Emit a Unit value to indicate completion
        }.flowOn(ioDispatcher) // Switch to the IO dispatcher for the operation
    }
    fun deleteAllTWRB(): Flow<Unit> {
        return flow {
            rateLocalDataSource.deleteAllWRB() // Call the delete function
            emit(Unit) // Emit a Unit value to indicate completion
        }.flowOn(ioDispatcher) // Switch to the IO dispatcher for the operation
    }

    fun deleteAllTWRC(): Flow<Unit> {
        return flow {
            rateLocalDataSource.deleteAllWRC() // Call the delete function
            emit(Unit) // Emit a Unit value to indicate completion
        }.flowOn(ioDispatcher) // Switch to the IO dispatcher for the operation
    }
}