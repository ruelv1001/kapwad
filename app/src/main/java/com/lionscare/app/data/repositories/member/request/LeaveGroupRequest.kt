package com.lionscare.app.data.repositories.member.request

data class LeaveGroupRequest(
    var group_id: String? = null,
    var user_id: String? = null
)