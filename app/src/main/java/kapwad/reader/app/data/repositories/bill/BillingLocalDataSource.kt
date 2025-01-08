package kapwad.reader.app.data.repositories.bill


import android.util.Log
import kapwad.reader.app.data.local.BillingDao

import kapwad.reader.app.data.model.CreatedBillListModelData
import kapwad.reader.app.data.model.MeterReaderListModelData
import kapwad.reader.app.data.model.ProductOrderListModelData
import javax.inject.Inject

class BillingLocalDataSource @Inject constructor(private val billingDao: BillingDao) {

    suspend fun createBilling(billing: CreatedBillListModelData) {
        billingDao.createBilling(billing)
    }

    suspend fun insertBilling(billing: CreatedBillListModelData) {

        billingDao.insertBilling(billing)
    }

//    suspend fun getAllUsers(): List<ProductOrderListModelData>{
//        return billingDao.getAllUsers()
//    }

    suspend fun getBilling() =
        billingDao.getBilling()

//    suspend fun getTotal() =
//        billingDao.getOverallTotal()


    suspend fun deleteAll() =
        billingDao.deleteAllBilling()

    suspend fun getValidatedExistingBill(month: String, owners_id: String): CreatedBillListModelData? =
        billingDao.getValidatedExistingBill(month,owners_id)


    suspend fun searchConsumer(searchQuery: String) =
        billingDao.searchConsumer("%$searchQuery%")

}