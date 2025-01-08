package kapwad.reader.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kapwad.reader.app.data.model.ConsumerListModelData
import kapwad.reader.app.data.model.CreatedBillListModelData
import kapwad.reader.app.data.model.MeterReaderListModelData

import kapwad.reader.app.data.model.ProductOrderListModelData

@Dao
interface BillingDao {


    @Insert
    suspend fun createBilling(billing: CreatedBillListModelData)

    @Insert
    suspend fun insertBilling(billing: CreatedBillListModelData)



    @Query("SELECT * FROM tbl_bill")
    suspend fun getBilling(): List<CreatedBillListModelData>

//    @Query("""
//        SELECT SUM(CAST(amount AS REAL) * CAST(quantity AS REAL))
//        FROM tbl_order
//        WHERE amount IS NOT NULL AND quantity IS NOT NULL
//    """)
//    suspend fun getOverallTotal(): Double?

    @Transaction
    @Query("DELETE FROM tbl_bill")
    suspend fun deleteAllBilling()

    @Query("SELECT * FROM tbl_bill WHERE month = :month AND meternumber = :meternumber")
    suspend fun getValidatedExistingBill(month: String, meternumber: String): CreatedBillListModelData?


    @Query("SELECT * FROM tbl_consumersaccounttb WHERE consumersid = :id")
    suspend fun getConsumerDetailsById(id: String): CreatedBillListModelData?

    @Query("SELECT * FROM tbl_consumersaccounttb WHERE " +
            "LOWER(Meternumber) LIKE LOWER(:searchQuery)")
    suspend fun searchConsumer(searchQuery: String): List<CreatedBillListModelData>
}