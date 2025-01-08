package kapwad.reader.app.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kapwad.reader.app.data.model.ConsumerListModelData
import kapwad.reader.app.data.model.CreatedBillListModelData

import kapwad.reader.app.data.model.ProductOrderListModelData

@Dao
interface ConsumerDao {

    @Insert
    suspend fun createConsumer(billing: ConsumerListModelData)

    @Insert
    suspend fun insertConsumer(billing: ConsumerListModelData)



    @Query("SELECT * FROM tbl_consumersaccounttb")
    suspend fun getConsumer(): List<ConsumerListModelData>


    @Transaction
    @Query("DELETE FROM tbl_consumersaccounttb")
    suspend fun deleteAllConsumer()



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConsumers(consumers: List<ConsumerListModelData>)

    @Query("SELECT * FROM tbl_consumersaccounttb WHERE consumersid = :meternumber")
    suspend fun getConsumerDetailsById(meternumber: String): ConsumerListModelData?

    @Query("SELECT * FROM tbl_consumersaccounttb WHERE " +
            "LOWER(lastName) LIKE LOWER(:searchQuery) OR " +
            "LOWER(firstName) LIKE LOWER(:searchQuery) OR " +
            "LOWER(middleName) LIKE LOWER(:searchQuery)")
    suspend fun searchConsumer(searchQuery: String): List<ConsumerListModelData>

}