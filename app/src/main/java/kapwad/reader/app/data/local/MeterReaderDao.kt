package kapwad.reader.app.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kapwad.reader.app.data.model.ConsumerListModelData
import kapwad.reader.app.data.model.CreatedBillListModelData
import kapwad.reader.app.data.model.MeterReaderListModelData

import kapwad.reader.app.data.model.ProductOrderListModelData
import kapwad.reader.app.data.model.RateAListModelData
import kapwad.reader.app.data.model.RateCListModelData
import kapwad.reader.app.data.model.RateListModelData
import kapwad.reader.app.data.model.TempListModelData

@Dao
interface MeterReaderDao {





    @Query("SELECT * FROM tbl_meterreaderaccountb")
    suspend fun get(): List<MeterReaderListModelData>


    @Transaction
    @Query("DELETE FROM tbl_meterreaderaccountb")
    suspend fun deleteAll()



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(temp: List<MeterReaderListModelData>)

    @Query("SELECT * FROM tbl_meterreaderaccountb WHERE mrid = :id")
    suspend fun getAllMeterDetailsById(id: String): MeterReaderListModelData?

    @Query("SELECT * FROM tbl_meterreaderaccountb WHERE username = :username AND password = :password")
    suspend fun getMeterDetailsByCredentials(username: String, password: String): MeterReaderListModelData?
}