package kapwad.reader.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kapwad.reader.app.data.model.CreatedBillListModelData

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
}