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
import kapwad.reader.app.data.model.RateAListModelData
import kapwad.reader.app.data.model.RateBListModelData
import kapwad.reader.app.data.model.RateCListModelData
import kapwad.reader.app.data.model.RateListModelData
import kapwad.reader.app.data.model.TempListModelData

@Dao
interface RateBDao {





    @Query("SELECT * FROM tbl_wr_commercial_b")
    suspend fun get(): List<RateBListModelData>


    @Transaction
    @Query("DELETE FROM tbl_wr_commercial_b")
    suspend fun deleteAll()



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(temp: List<RateBListModelData>)

    @Query("SELECT * FROM tbl_wr_commercial_b WHERE id = :id")
    suspend fun getRateBDetailsById(id: String): RateBListModelData?
}