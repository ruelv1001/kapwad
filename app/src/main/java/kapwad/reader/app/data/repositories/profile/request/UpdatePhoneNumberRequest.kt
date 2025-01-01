package kapwad.reader.app.data.repositories.profile.request

import androidx.annotation.Keep

@Keep
data class UpdatePhoneNumberRequest(
    val phone_number: String? = null,
    val phone_number_country_code: String? = null,
    val hint : Boolean? = false
)

@Keep
data class UpdatePhoneNumberOTPRequest(
    val phone_number: String? = null,
    val otp: String? = null,
)
