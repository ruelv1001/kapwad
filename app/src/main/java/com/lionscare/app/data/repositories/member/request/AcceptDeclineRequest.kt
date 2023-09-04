package com.lionscare.app.data.repositories.member.request

data class AcceptDeclineRequest(
    var pending_id: String,
    var group_id: String
)