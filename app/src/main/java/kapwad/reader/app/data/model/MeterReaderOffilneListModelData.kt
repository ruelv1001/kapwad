package kapwad.reader.app.data.model

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize

@Entity(tableName = "tbl_meterreaderaccountb_offline")
data class MeterReaderOfflineListModelData(
    @PrimaryKey
    var mrid : Int? = null,
    var date: String? = null,
    var firstname: String? = null,
    var middlename: String? = null,
    var lastname: String? = null,
    var username: String? = null,
    var password: String? = null,
): Parcelable


data class MeterReaderOfflineListResponse(
    val temp: List<MeterReaderOfflineListModelData>
)