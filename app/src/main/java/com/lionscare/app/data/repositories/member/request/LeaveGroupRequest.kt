package com.lionscare.app.data.repositories.member.request

data class LeaveGroupRequest(
    var group_id: Int,
    var user_id: String
)