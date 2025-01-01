package kapwad.reader.app.data.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class SyncListModelData(
    var id: String?=null,
    val biller_month : String? = null,
    val status : String? = null,
    val uploaded : String? = null,
    val submitted : String? = null,
): Parcelable
