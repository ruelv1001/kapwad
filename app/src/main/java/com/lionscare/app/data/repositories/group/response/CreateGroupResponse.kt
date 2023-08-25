package com.lionscare.app.data.repositories.group.response

import androidx.annotation.Keep
import com.lionscare.app.data.repositories.baseresponse.DateModel
import com.lionscare.app.data.repositories.baseresponse.Meta

@Keep
data class CreateGroupResponse(
    var data: Data? = null,
    var meta: Meta? = null,
    var msg: String? = null,
    var status: Boolean? = false,
    var status_code: String? = null
)

@Keep
data class Data(
    var date_created: DateModel? = null,
    var group_approval: Boolean? = false,
    var group_name: String? = null,
    var group_privacy: String? = null,
    var group_type: String? = null,
    var id: Int? = 0,
    var owner_user_id: String? = null
)
