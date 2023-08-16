package com.lionscare.app.data.repositories.registration.request

import androidx.annotation.Keep

@Keep
data class RegistrationRequest(
    var firstname: String? = null,
    var lastname: String? = null,
    var middlename: String? = null,
    var phone_number: String? = null,
    var password: String? = null,
    var password_confirmation: String? = null,
    var otp: String? = null
)
