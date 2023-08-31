package com.lionscare.app.data.repositories.wallet.response


import androidx.annotation.Keep

@Keep
data class ScanQRResponse(
    val data: QRData? = null,
    val msg: String? = null,
    val status: Boolean? = false,
    val status_code: String? = null
)

@Keep
data class QRData(
    val id: String? = null,
    val name: String? = null,
    val phone_number: String? = null
)