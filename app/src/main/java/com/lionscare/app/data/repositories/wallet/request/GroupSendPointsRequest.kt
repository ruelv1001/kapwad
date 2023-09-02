package com.lionscare.app.data.repositories.wallet.request


import androidx.annotation.Keep

@Keep
data class GroupSendPointsRequest(
    val group_id: String,
    val amount: String,
    val receiver_group_id: String? = null,
    val receiver_user_id: String? = null
)