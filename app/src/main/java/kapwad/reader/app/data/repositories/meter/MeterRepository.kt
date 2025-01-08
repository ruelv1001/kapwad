package kapwad.reader.app.data.repositories.meter

import kapwad.reader.app.data.model.ConsumerListModelData
import kapwad.reader.app.data.model.TempListModelData
import kapwad.reader.app.data.model.CreatedBillListModelData
import kapwad.reader.app.data.model.MeterReaderListModelData
import kapwad.reader.app.data.model.OtherListModelData
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

class MeterRepository @Inject constructor(
    private val meterRemoteDataSource: MeterRemoteDataSource,
    private val meterLocalDataSource: MeterLocalDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {


    fun getAllMeter(): Flow<List<MeterReaderListModelData>> {
        return flow {
            val response = meterLocalDataSource.getMeterReader()
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun getAllMeterOnline(): Flow<List<MeterReaderListModelData>> {
        return flow {
            val response = meterRemoteDataSource.getMeterList()
            emit(response)
        }.flowOn(ioDispatcher)
    }


    //---------------------------------------


    fun create(rate: List<MeterReaderListModelData>): Flow<List<MeterReaderListModelData>> {
        return flow {
            meterLocalDataSource.insertMeter(rate)
            emit(rate)
        }.flowOn(ioDispatcher)
    }


    fun getOther(): Flow<List<MeterReaderListModelData>> {
        return flow {
            val response = meterLocalDataSource.getMeterReader()
            emit(response)
        }.flowOn(ioDispatcher)
    }




    //-----------------------------------


    fun deleteAllMeter(): Flow<Unit> {
        return flow {
            meterLocalDataSource.deleteAlMeterReader() // Call the delete function
            emit(Unit) // Emit a Unit value to indicate completion
        }.flowOn(ioDispatcher) // Switch to the IO dispatcher for the operation
    }
    fun getMeterById(id: String): Flow<MeterReaderListModelData?> {
        return flow {
            val response = meterLocalDataSource.getMeterDetailsById(id)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun getMeterByCredential(username:String,password:String): Flow<MeterReaderListModelData?> {
        return flow {
            val response = meterLocalDataSource.getMeterDetailsByAccount(username,password)
            emit(response)
        }.flowOn(ioDispatcher)
    }

}