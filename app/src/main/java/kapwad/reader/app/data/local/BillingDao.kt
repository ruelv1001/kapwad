package kapwad.reader.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

import kapwad.reader.app.data.model.ProductOrderListModelData

@Dao
interface OrderDao {


    @Insert
    suspend fun createOrder(order: ProductOrderListModelData)

    @Insert
    suspend fun insertOrder(order: ProductOrderListModelData)



    @Query("SELECT * FROM tbl_order")
    suspend fun getOrders(): List<ProductOrderListModelData>

//    @Query("""
//        SELECT SUM(CAST(amount AS REAL) * CAST(quantity AS REAL))
//        FROM tbl_order
//        WHERE amount IS NOT NULL AND quantity IS NOT NULL
//    """)
//    suspend fun getOverallTotal(): Double?

    @Transaction
    @Query("DELETE FROM tbl_order")
    suspend fun deleteAllOrders()
}