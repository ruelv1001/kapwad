package com.lionscare.app.data.repositories.profile.request

import androidx.annotation.Keep

@Keep
data class UpdatePhoneNumberRequest(
    val phone_number: String? = null
)

@Keep
data class UpdatePhoneNumberOTPRequest(
    val phone_number: String? = null,
    val otp: String? = null,
)
