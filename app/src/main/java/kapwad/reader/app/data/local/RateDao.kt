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
import kapwad.reader.app.data.model.RateListModelData
import kapwad.reader.app.data.model.TempListModelData

@Dao
interface RateDao {





    @Query("SELECT * FROM tbl_wr_commercial")
    suspend fun get(): List<RateListModelData>


    @Transaction
    @Query("DELETE FROM tbl_wr_commercial")
    suspend fun deleteAll()



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(temp: List<RateListModelData>)


    @Query("SELECT * FROM tbl_wr_commercial WHERE id = :id")
    suspend fun getRateDetailsById(id: String): RateListModelData?
}