package com.lionscare.app.data.repositories.group.response

import android.os.Parcelable
import androidx.annotation.Keep
import com.lionscare.app.data.repositories.baseresponse.DateModel
import com.lionscare.app.data.repositories.baseresponse.Meta
import com.lionscare.app.data.repositories.baseresponse.QrValue
import com.lionscare.app.data.repositories.baseresponse.UserModel
import kotlinx.parcelize.Parcelize
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
    var name: String? = null,
    var type: String? = null,
    var privacy: String? = null,
    var is_member: Boolean? = false,
    var is_admin: Boolean? = false,
    var with_approval: Boolean? = false,
    var owner_user_id: String? = null,
    var date_created: DateModel? = null,
    var owner: UserModel? = null,
) : Parcelable
