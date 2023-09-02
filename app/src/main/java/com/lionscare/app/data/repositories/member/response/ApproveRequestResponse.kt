package com.lionscare.app.data.repositories.member.response

import com.lionscare.app.data.repositories.baseresponse.Meta

data class ApproveRequestResponse(
    var data: MemberListData? = null,
    var has_morepage: Boolean? = null,
    var meta: Meta? = null,
    var msg: String? = null,
    var status: Boolean? = null,
    var status_code: String? = null,
    var total: Int? = null
)
