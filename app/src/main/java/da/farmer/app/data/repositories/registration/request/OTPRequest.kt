package da.farmer.app.data.repositories.registration.request

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class OTPRequest(
    var phone_number: String? = null
): Parcelable
