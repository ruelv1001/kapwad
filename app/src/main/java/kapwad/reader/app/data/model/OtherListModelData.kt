package kapwad.reader.app.data.model

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize

@Entity(tableName = "tbl_other_charges")
data class OtherListModelData(
    @PrimaryKey
    var id: Int? = null,
    val wmms: String? = null,
    val convenience_fee: String? = null,
    val penalty: String? = null,
    val minimum_waterrate: String? = null,
    val franchise_tax: String? = null,
    val poca: String? = null,
): Parcelable


data class OtherListResponse(
    val temp: List<OtherListModelData>
)