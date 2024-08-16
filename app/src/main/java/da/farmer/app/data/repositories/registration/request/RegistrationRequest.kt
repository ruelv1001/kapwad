package da.farmer.app.data.repositories.registration.request

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class RegistrationRequest(
    var username : String? = null,
    var firstname: String? = null,
    var lastname: String? = null,
    var middlename: String? = null,
    var phone_number: String? = null,
    var phone_number_country_code: String? = null,
    var password: String? = null,
    var password_confirmation: String? = null,
    var otp: String? = null
): Parcelable
