package kapwad.reader.app.data.repositories.wallet.response


import android.os.Parcelable
import androidx.annotation.Keep
import kapwad.reader.app.data.repositories.baseresponse.Avatar
import kotlinx.parcelize.Parcelize

@Keep
data class ScanQRResponse(
    val data: QRData? = null,
    val msg: String? = null,
    val status: Boolean? = false,
    val status_code: String? = null
)

@Keep
@Parcelize
data class QRData(
    val id: String? = null,
    val name: String? = null,
    val phone_number: String? = null,
    val amount: String? = null,
    val avatar: Avatar? = null
): Parcelable