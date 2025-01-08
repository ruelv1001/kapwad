package kapwad.reader.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import kapwad.reader.app.data.model.ConsumerListModelData
import kapwad.reader.app.data.model.CreatedBillListModelData
import kapwad.reader.app.data.model.MeterReaderListModelData
import kapwad.reader.app.data.model.OtherListModelData
import kapwad.reader.app.data.model.ProductOrderListModelData
import kapwad.reader.app.data.model.RateAListModelData
import kapwad.reader.app.data.model.RateBListModelData
import kapwad.reader.app.data.model.RateCListModelData
import kapwad.reader.app.data.model.RateListModelData
import kapwad.reader.app.data.model.RateReListModelData
import kapwad.reader.app.data.model.TempListModelData


@Database(entities = [UserLocalData::class, ProductOrderListModelData::class, CreatedBillListModelData::class,
    ConsumerListModelData::class,  TempListModelData::class,  RateListModelData::class
    ,  RateAListModelData::class
    ,  RateBListModelData::class
    ,  RateCListModelData::class
    ,  OtherListModelData::class
    ,  RateReListModelData::class
    ,  MeterReaderListModelData::class], version = 3, exportSchema = false)
abstract class BoilerPlateDatabase : RoomDatabase(){
    abstract val userDao : UserDao
    abstract val orderDao : OrderDao
    abstract val billingDao : BillingDao
    abstract val consumerDao : ConsumerDao
    abstract val tempDao : TempDao
    abstract val rateDao : RateDao
    abstract val rateADao : RateADao
    abstract val rateBDao : RateBDao
    abstract val rateCDao : RateCDao
    abstract val rateReDao : RateReDao
    abstract val otherChargesDao : OtherChargesDao
    abstract val meterReaderDao : MeterReaderDao
}