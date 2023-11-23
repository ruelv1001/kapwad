package com.ziacare.app.data.repositories.member.response

import androidx.annotation.Keep
import com.ziacare.app.data.repositories.baseresponse.Meta

@Keep
data class ApproveRequestResponse(
    var data: MemberListData? = null,
    var has_morepage: Boolean? = null,
    var meta: Meta? = null,
    var msg: String? = null,
    var status: Boolean? = null,
    var status_code: String? = null,
    var total: Int? = null
)
