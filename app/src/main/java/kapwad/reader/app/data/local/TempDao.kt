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
import kapwad.reader.app.data.model.TempListModelData

@Dao
interface TempDao {





    @Query("SELECT * FROM tbl_consumersaccounttb")
    suspend fun getTemp(): List<TempListModelData>


    @Transaction
    @Query("DELETE FROM tbl_tempo_bill")
    suspend fun deleteAllTemp()



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTempOffline(temp: List<TempListModelData>)

    @Query("SELECT * FROM tbl_tempo_bill WHERE account_number = :meternumber")
    suspend fun getTempDetailsById(meternumber: String): TempListModelData?
}