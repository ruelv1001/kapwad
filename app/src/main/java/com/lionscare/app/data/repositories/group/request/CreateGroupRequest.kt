package com.lionscare.app.data.repositories.group.request


import androidx.annotation.Keep

@Keep
data class CreateGroupRequest(
    var group_id: Int? = null,
    var group_approval: Int? = 0,
    var group_name: String? = null,
    var group_passcode: String? = null,
    var group_privacy: String? = null,
    var group_type: String? = null
)