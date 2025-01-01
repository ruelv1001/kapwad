package kapwad.reader.app.data.repositories.waterrate


import android.util.Log
import kapwad.reader.app.data.local.BillingDao
import kapwad.reader.app.data.local.ConsumerDao
import kapwad.reader.app.data.local.RateADao
import kapwad.reader.app.data.local.RateBDao
import kapwad.reader.app.data.local.RateCDao
import kapwad.reader.app.data.local.RateDao
import kapwad.reader.app.data.local.TempDao
import kapwad.reader.app.data.model.ConsumerListModelData

import kapwad.reader.app.data.model.CreatedBillListModelData
import kapwad.reader.app.data.model.ProductOrderListModelData
import kapwad.reader.app.data.model.RateAListModelData
import kapwad.reader.app.data.model.RateBListModelData
import kapwad.reader.app.data.model.RateCListModelData
import kapwad.reader.app.data.model.RateListModelData
import kapwad.reader.app.data.model.TempListModelData
import javax.inject.Inject

class RateLocalDataSource @Inject constructor(private val rateDao: RateDao,private val rateADao: RateADao,
                                              private val rateBDao: RateBDao,private val rateCDao: RateCDao
) {


    suspend fun insertWR(rateListModelData: List<RateListModelData>) {
        rateDao.insert(rateListModelData)
    }

    suspend fun getWR() =
        rateDao.get()

    suspend fun deleteAllWR() =
        rateDao.deleteAll()

    ////on top is default water rani


    suspend fun insertWRA(rateAListModelData: List<RateAListModelData>) {
        rateADao.insert(rateAListModelData)
    }

    suspend fun getWRA() =
        rateADao.get()

    suspend fun deleteAllWRA() =
        rateADao.deleteAll()

    ////on top is Water A rate rana




    suspend fun insertWRB(rateBListModelData: List<RateBListModelData>) {
        rateBDao.insert(rateBListModelData)
    }

    suspend fun getWRB() =
        rateBDao.get()

    suspend fun deleteAllWRB() =
        rateBDao.deleteAll()

    ////on top is Water B rate rana

    suspend fun insertWRC(rateCListModelData: List<RateCListModelData>) {
        rateCDao.insert(rateCListModelData)
    }

    suspend fun getWRC() =
        rateCDao.get()

    suspend fun deleteAllWRC() =
        rateCDao.deleteAll()

    ////on top is Water C rate rana

}