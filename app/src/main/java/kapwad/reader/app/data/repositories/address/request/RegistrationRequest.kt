package kapwad.reader.app.data.repositories.address.request

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class RegistrationRequest(
    var step: String? = null,
    var code: String? = null,
    var firstname: String? = null,
    var lastname: String? = null,
    var username: String? = null,
    var phone_number: String? = null,
    var otp: String? = null,
    var province_sku: String? = null,
    var province_name: String? = null,
    var city_sku: String? = null,
    var city_name: String? = null,
    var brgy_sku: String? = null,
    var brgy_name: String? = null,
    var street_name: String? = null,
    var zipcode: String? = null,
    var email: String? = null,
    var password: String? = null,
    var password_confirmation: String? = null
): Parcelable