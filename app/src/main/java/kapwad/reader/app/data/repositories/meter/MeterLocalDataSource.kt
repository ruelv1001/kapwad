package kapwad.reader.app.data.repositories.meter


import android.util.Log
import kapwad.reader.app.data.local.BillingDao
import kapwad.reader.app.data.local.ConsumerDao
import kapwad.reader.app.data.local.MeterReaderDao

import kapwad.reader.app.data.local.RateADao
import kapwad.reader.app.data.local.RateBDao
import kapwad.reader.app.data.local.RateCDao
import kapwad.reader.app.data.local.RateDao
import kapwad.reader.app.data.local.TempDao
import kapwad.reader.app.data.model.ConsumerListModelData

import kapwad.reader.app.data.model.CreatedBillListModelData
import kapwad.reader.app.data.model.MeterReaderListModelData
import kapwad.reader.app.data.model.OtherListModelData
import kapwad.reader.app.data.model.ProductOrderListModelData
import kapwad.reader.app.data.model.RateAListModelData
import kapwad.reader.app.data.model.RateBListModelData
import kapwad.reader.app.data.model.RateCListModelData
import kapwad.reader.app.data.model.RateListModelData
import kapwad.reader.app.data.model.TempListModelData
import javax.inject.Inject

class MeterLocalDataSource @Inject constructor(private val meterReaderDao: MeterReaderDao
) {


    suspend fun insertMeter(meterReaderListModelData: List<MeterReaderListModelData>) {
        meterReaderDao.insert(meterReaderListModelData)
    }






    suspend fun getMeterReader() =
        meterReaderDao.get()

    suspend fun deleteAlMeterReader() =
        meterReaderDao.deleteAll()

    suspend fun getMeterDetailsById(id: String): MeterReaderListModelData? =
        meterReaderDao.getAllMeterDetailsById(id)

    suspend fun getMeterDetailsByAccount(username: String,password: String): MeterReaderListModelData? =
        meterReaderDao.getMeterDetailsByCredentials(username,password)

}