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
    var id: String? = null,
    var name: String? = null,
    var type: String? = null,
    var privacy: String? = null,
    var with_approval: Boolean? = false,
    var owner_user_id: String? = null,
    var date_created: DateModel? = null,
    var owner: UserModel? = null,
) : Serializable
