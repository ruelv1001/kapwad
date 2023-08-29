package com.lionscare.app.data.repositories.group.response


import androidx.annotation.Keep
import com.lionscare.app.data.repositories.baseresponse.DateModel
import com.lionscare.app.data.repositories.baseresponse.Meta
import com.lionscare.app.data.repositories.baseresponse.UserModel

@Keep
data class GetGroupListResponse(
    var data: List<GroupListData>? = null,
    var has_morepages: Boolean? = false,
    var meta: Meta? = null,
    var msg: String? = null,
    var status: Boolean? = false,
    var status_code: String? = null,
    var total: Int? = 0
)

@Keep
data class GroupListData(
    var date_created: DateModel? = null,
    var group_approval: Boolean? = false,
    var group_name: String? = null,
    var group_privacy: String? = null,
    var group_type: String? = null,
    var id: Int? = null,
    var owner: UserModel? = null,
    var owner_user_id: String? = null
)

