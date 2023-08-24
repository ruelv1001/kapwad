package com.lionscare.app.data.repositories.auth.request

data class LoginRequest(
    val phone_number: String,
    val password: String
)