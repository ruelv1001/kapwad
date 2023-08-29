package com.lionscare.app.data.repositories.wallet.response


import androidx.annotation.Keep

@Keep
data class TransactionDetailsResponse(
    val data: TransactionData? = null,
    val msg: String? = null,
    val status: Boolean? = null,
    val status_code: String? = null
)