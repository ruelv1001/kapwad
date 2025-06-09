package kapwad.reader.app.data.repositories.consumers


import android.util.Log
import kapwad.reader.app.data.local.BillingDao
import kapwad.reader.app.data.local.ConsumerDao
import kapwad.reader.app.data.model.ConsumerListModelData

import kapwad.reader.app.data.model.CreatedBillListModelData
import kapwad.reader.app.data.model.ProductOrderListModelData
import javax.inject.Inject

class ConsumerLocalDataSource @Inject constructor(private val consumerDao: ConsumerDao) {

    suspend fun insertConsumers(consumers: List<ConsumerListModelData>) {
        consumerDao.insertConsumers(consumers)
    }




    suspend fun getConsumerDetailsById(id: String): ConsumerListModelData? =
        consumerDao.getConsumerDetailsById(id)

    suspend fun getConsumerDetailsByEachId(id: String): ConsumerListModelData? =
        consumerDao.getConsumerDetailsByEachId(id)


//    suspend fun getAllUsers(): List<ProductOrderListModelData>{
//        return billingDao.getAllUsers()
//    }

    suspend fun getConsumer() =
        consumerDao.getConsumer()

//    suspend fun getTotal() =
//        billingDao.getOverallTotal()


    suspend fun deleteAll() =
        consumerDao.deleteAllConsumer()



    suspend fun searchConsumer(searchQuery: String) =
        consumerDao.searchConsumer("%$searchQuery%")

}