package kapwad.reader.app.data.repositories.address.request

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class MunicipalityListRequest(
    var reference: String,
    var type : String = "id"
) : Parcelable