package kapwad.reader.app.data.repositories.group.response

import android.os.Parcelable
import androidx.annotation.Keep
import kapwad.reader.app.data.repositories.baseresponse.Avatar
import kapwad.reader.app.data.repositories.baseresponse.DateModel
import kapwad.reader.app.data.repositories.baseresponse.Meta
import kapwad.reader.app.data.repositories.baseresponse.QrValue
import kapwad.reader.app.data.repositories.baseresponse.UserModel
import kotlinx.parcelize.Parcelize

@Keep
data class CreateGroupResponse(
    var data: GroupData? = null,
    var meta: Meta? = null,
    var msg: String? = null,
    var status: Boolean? = false,
    var status_code: String? = null
)

@Keep
@Parcelize
data class GroupData(
    var id: String? = null,
    var code: String? = null,
    var qrcode: String? = null,
    var qr_value: QrValue? = null,
    var qrcode_value: String? = null,
    var member_count: Int? = null,
    var member_only_count: Int? = null,
    var admin_only_count: Int? = null,
    var pending_requests_count: Int? = null,
    var request_assistance_count: Int? = null,
    var name: String? = null,
    var type: String? = null,
    var privacy: String? = null,
    var is_member: Boolean? = false,
    var is_admin: Boolean? = false,
    var with_approval: Boolean? = false,
    var owner_user_id: String? = null,
    var date_created: DateModel? = null,
    var owner: UserModel? = null,
    var avatar : Avatar? = null
) : Parcelable
