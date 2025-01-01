package kapwad.reader.app.data.repositories.others


import android.util.Log
import kapwad.reader.app.data.local.BillingDao
import kapwad.reader.app.data.local.ConsumerDao
import kapwad.reader.app.data.local.OtherChargesDao
import kapwad.reader.app.data.local.RateADao
import kapwad.reader.app.data.local.RateBDao
import kapwad.reader.app.data.local.RateCDao
import kapwad.reader.app.data.local.RateDao
import kapwad.reader.app.data.local.TempDao
import kapwad.reader.app.data.model.ConsumerListModelData

import kapwad.reader.app.data.model.CreatedBillListModelData
import kapwad.reader.app.data.model.OtherListModelData
import kapwad.reader.app.data.model.ProductOrderListModelData
import kapwad.reader.app.data.model.RateAListModelData
import kapwad.reader.app.data.model.RateBListModelData
import kapwad.reader.app.data.model.RateCListModelData
import kapwad.reader.app.data.model.RateListModelData
import kapwad.reader.app.data.model.TempListModelData
import javax.inject.Inject

class OtherLocalDataSource @Inject constructor(private val otherChargesDao: OtherChargesDao
) {


    suspend fun insertOther(otherListModelData: List<OtherListModelData>) {
        otherChargesDao.insert(otherListModelData)
    }






    suspend fun getOtherCharges() =
        otherChargesDao.get()

    suspend fun deleteAlOther() =
        otherChargesDao.deleteAll()

    suspend fun getOtherDetailsById(id: String): OtherListModelData? =
        otherChargesDao.getOtherDetailsById(id)

}