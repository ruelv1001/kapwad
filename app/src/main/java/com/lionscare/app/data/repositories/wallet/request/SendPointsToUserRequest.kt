package com.lionscare.app.data.repositories.wallet.request


import androidx.annotation.Keep

@Keep
data class SendPointsToUserRequest(
    val amount: String,
    val user_id: String
)