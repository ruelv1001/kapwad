package kapwad.reader.app.data.repositories.registration.request


import androidx.annotation.Keep

@Keep
data class OnboardingRequest(
    var code: String? = null,
    var otp: String? = null,
    var password: String? = null,
    var password_confirmation: String? = null
)