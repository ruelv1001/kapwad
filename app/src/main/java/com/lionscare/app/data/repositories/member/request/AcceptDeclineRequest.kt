package com.lionscare.app.data.repositories.member.request

import androidx.annotation.Keep

@Keep
data class AcceptDeclineRequest(
    var pending_id: Long,
    var group_id: String
)