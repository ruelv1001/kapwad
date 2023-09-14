package com.lionscare.app.data.repositories.wallet.response


import androidx.annotation.Keep

@Keep
data class GetBalanceResponse(
    val data: BalanceData? = null,
    val msg: String? = null,
    val status: Boolean? = false,
    val status_code: String? = null
)

@Keep
data class BalanceData(
    val id: String? = null,
    val raw_value: Double? = 0.0,
    val value: String? = null,
    val group_name: String? = null
)