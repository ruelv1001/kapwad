package com.ziacare.app.data.repositories.group.response


import androidx.annotation.Keep
import com.ziacare.app.data.repositories.baseresponse.Meta

@Keep
data class ImmediateFamilyResponse(
    var data: GroupListData? = null,
    var meta: Meta? = null,
    var msg: String? = null,
    var status: Boolean? = false,
    var status_code: String? = null
)
