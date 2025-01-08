package kapwad.reader.app.data.model

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@Entity(tableName = "tbl_order")
data class ProductOrderListModelData(
    @PrimaryKey(autoGenerate = true) var id: Int? = null,

    var consumer_name: String? = null,
    var consumer_address: String? = null,
    val biller_month: String? = null,
    val previous_reading: String? = null,
    val present_reading: String? = null,

    val status : String? = null,
    val uploaded : String? = null,
    val submitted : String? = null,
    val meter_reader: String? = null,
    val zone: String? = null,
    var username : String? = null,
    var password : String? = null,
    var isLogin : String? = null,
): Parcelable
