package com.lionscare.app.data.repositories.admin.request

import androidx.annotation.Keep

@Keep
data class AdminRequest(
    val member_id: Int,
    val group_id: String
)