package com.ziacare.app.data.repositories.wallet.request


import androidx.annotation.Keep

@Keep
data class TopupRequest(
    val amount: String
)