package kapwad.reader.app.data.repositories.temp


import android.util.Log
import kapwad.reader.app.data.local.BillingDao
import kapwad.reader.app.data.local.ConsumerDao
import kapwad.reader.app.data.local.TempDao
import kapwad.reader.app.data.model.ConsumerListModelData

import kapwad.reader.app.data.model.CreatedBillListModelData
import kapwad.reader.app.data.model.ProductOrderListModelData
import kapwad.reader.app.data.model.TempListModelData
import javax.inject.Inject

class TempLocalDataSource @Inject constructor(private val tempDao: TempDao) {


    suspend fun insertTemp(tempListModelData: List<TempListModelData>) {
        tempDao.insertTempOffline(tempListModelData)
    }





    suspend fun getTemp() =
        tempDao.getTemp()




    suspend fun deleteAll() =
        tempDao.deleteAllTemp()


    suspend fun getTempDetailsById(id: String): TempListModelData? =
        tempDao.getTempDetailsById(id)
}