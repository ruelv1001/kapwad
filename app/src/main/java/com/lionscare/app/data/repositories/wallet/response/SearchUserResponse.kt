package com.lionscare.app.data.repositories.wallet.response


import androidx.annotation.Keep

@Keep
data class SearchUserResponse(
    val data: List<QRData>? = null,
    val msg: String? = null,
    val status: Boolean? = null,
    val status_code: String? = null
)