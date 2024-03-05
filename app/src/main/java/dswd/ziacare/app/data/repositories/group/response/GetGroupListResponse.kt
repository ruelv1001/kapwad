package dswd.ziacare.app.data.repositories.group.response


import androidx.annotation.Keep
import dswd.ziacare.app.data.repositories.baseresponse.Avatar
import dswd.ziacare.app.data.repositories.baseresponse.DateModel
import dswd.ziacare.app.data.repositories.baseresponse.Meta
import dswd.ziacare.app.data.repositories.baseresponse.QrValue
import dswd.ziacare.app.data.repositories.baseresponse.UserModel

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
    var id: String? = null,
    var code: String? = null,
    var qrcode: String? = null,
    var qr_value: QrValue? = null,
    var qrcode_vale: String? = null,
    var is_member: Boolean? = false,
    var is_admin: Boolean? = false,
    var member_count: Int? = null,
    var member_only_count: Int? = null,
    var admin_only_count: Int? = null,
    var pending_requests_count: Int? = null,
    var name: String? = null,
    var type: String? = null,
    var privacy: String? = null,
    var with_approval: Boolean? = false,
    var owner_user_id: String? = null,
    var date_created: DateModel? = null,
    var owner: UserModel? = null,
    var avatar : Avatar? = null
)

