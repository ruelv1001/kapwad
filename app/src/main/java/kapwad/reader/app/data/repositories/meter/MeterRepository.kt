package kapwad.reader.app.data.repositories.others

import kapwad.reader.app.data.model.ConsumerListModelData
import kapwad.reader.app.data.model.TempListModelData
import kapwad.reader.app.data.model.CreatedBillListModelData
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

class OthersRepository @Inject constructor(
    private val othersRemoteDataSource: OthersRemoteDataSource,
    private val otherLocalDataSource: OtherLocalDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {


    fun getAllOther(): Flow<List<OtherListModelData>> {
        return flow {
            val response = othersRemoteDataSource.getRateList()
            emit(response)
        }.flowOn(ioDispatcher)
    }


    //---------------------------------------


    fun create(rate: List<OtherListModelData>): Flow<List<OtherListModelData>> {
        return flow {
            otherLocalDataSource.insertOther(rate)
            emit(rate)
        }.flowOn(ioDispatcher)
    }


    fun getOther(): Flow<List<OtherListModelData>> {
        return flow {
            val response = otherLocalDataSource.getOtherCharges()
            emit(response)
        }.flowOn(ioDispatcher)
    }




    //-----------------------------------


    fun deleteAllOther(): Flow<Unit> {
        return flow {
            otherLocalDataSource.deleteAlOther() // Call the delete function
            emit(Unit) // Emit a Unit value to indicate completion
        }.flowOn(ioDispatcher) // Switch to the IO dispatcher for the operation
    }
    fun getOtherById(id: String): Flow<OtherListModelData?> {
        return flow {
            val response = otherLocalDataSource.getOtherDetailsById(id)
            emit(response)
        }.flowOn(ioDispatcher)
    }

}