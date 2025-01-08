package kapwad.reader.app.data.model

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize

@Entity(tableName = "tbl_wr_commercial")
data class RateListModelData(
    @PrimaryKey
    var id: Int? = null,
    val wr_0_10: String? = null,
    val wr_11_20: String? = null,
    val wr_21_30: String? = null,
    val wr_31_40: String? = null,
    val wr_41_up: String? = null,

): Parcelable


data class RateListResponse(
    val temp: List<RateListModelData>
)