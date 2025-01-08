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
import kapwad.reader.app.data.model.RateReListModelData
import kapwad.reader.app.data.model.TempListModelData

@Dao
interface RateReDao {





    @Query("SELECT * FROM tbl_wr_residential")
    suspend fun get(): List<RateReListModelData>


    @Transaction
    @Query("DELETE FROM tbl_wr_residential")
    suspend fun deleteAll()



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rate: List<RateReListModelData>)


    @Query("SELECT * FROM tbl_wr_residential WHERE id = :id")
    suspend fun getRateReDetailsById(id: String): RateReListModelData?
}