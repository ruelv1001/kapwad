package com.lionscare.app.data.repositories.member.request


import androidx.annotation.Keep

@Keep
data class ListOfMembersRequest(
    var pending_id: String? = null,
    var group_id: String? = null,
    var passcode: String? = null,
    var page: String? = null,
    var type: String? = null
)