package kapwad.reader.app.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kapwad.reader.app.data.model.ConsumerListModelData
import kapwad.reader.app.data.model.CreatedBillListModelData
import kapwad.reader.app.data.model.OtherListModelData

import kapwad.reader.app.data.model.ProductOrderListModelData
import kapwad.reader.app.data.model.RateListModelData
import kapwad.reader.app.data.model.TempListModelData

@Dao
interface OtherChargesDao {





    @Query("SELECT * FROM tbl_other_charges")
    suspend fun get(): List<OtherListModelData>


    @Transaction
    @Query("DELETE FROM tbl_other_charges")
    suspend fun deleteAll()



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(temp: List<OtherListModelData>)

    @Query("SELECT * FROM tbl_other_charges WHERE id = :id")
    suspend fun getOtherDetailsById(id: String): OtherListModelData?
}