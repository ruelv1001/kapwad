package com.lionscare.app.data.repositories.auth.request

import androidx.annotation.Keep

@Keep
data class LoginRequest(
    val phone_number: String,
    val password: String
)