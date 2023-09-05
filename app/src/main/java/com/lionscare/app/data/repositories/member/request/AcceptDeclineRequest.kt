package com.lionscare.app.data.repositories.member.request

data class AcceptDeclineRequest(
    var pending_id: Long,
    var group_id: String
)