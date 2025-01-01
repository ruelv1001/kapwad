package kapwad.reader.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import kapwad.reader.app.data.model.ProductOrderListModelData


@Database(entities = [UserLocalData::class, ProductOrderListModelData::class], version = 3, exportSchema = false)
abstract class BoilerPlateDatabase : RoomDatabase(){
    abstract val userDao : UserDao
    abstract val orderDao : OrderDao
}