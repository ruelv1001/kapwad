package kapwad.reader.app.data.model

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize

@Entity(tableName = "tbl_tempo_bill")
data class TempListModelData(
    @PrimaryKey
    var id: Int? = null,
    val Address: String? = null,
    val Amount_Balance: String? = null,
    val Arrears_Deduct: String? = null,
    val Arrears_Description: String? = null,
    val Brand: String? = null,
    val Concessionaire: String? = null,
    val Date_Arrears: String? = null,
    val Date_Others: String? = null,
    val Meternumber: String? = null,
    val Num_of_Months: String? = null,
    val Others_Deduct: String? = null,
    val Others_Description: String? = null,
    val Prev: String? = null,
    val Service_Status: String? = null,
    val Stag_Arrears_Prev: String? = null,
    val Stag_Others_Prev: String? = null,
    val account_number: String? = null,
    val account_series: String? = null,
    val date_read_pay: String? = null,
    val status_type: String? = null,
    val zone: String? = null,
): Parcelable


data class TempListResponse(
    val temp: List<TempListModelData>
)