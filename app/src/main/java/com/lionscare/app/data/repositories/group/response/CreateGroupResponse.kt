package com.lionscare.app.data.repositories.group.response

import androidx.annotation.Keep
import com.lionscare.app.data.repositories.baseresponse.DateModel
import com.lionscare.app.data.repositories.baseresponse.Meta
import com.lionscare.app.data.repositories.baseresponse.UserModel
import java.io.Serializable

@Keep
data class CreateGroupResponse(
    var data: GroupData? = null,
    var meta: Meta? = null,
    var msg: String? = null,
    var status: Boolean? = false,
    var status_code: String? = null
)

@Keep
data class GroupData(
    var date_created: DateModel? = null,
    var group_approval: Boolean? = false,
    var group_name: String? = null,
    var group_privacy: String? = null,
    var group_type: String? = null,
    var id: String? = null,
    var owner: UserModel? = null,
    var owner_user_id: String? = null
) : Serializable
