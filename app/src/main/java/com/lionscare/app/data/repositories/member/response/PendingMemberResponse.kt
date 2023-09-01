package com.lionscare.app.data.repositories.member.response


import androidx.annotation.Keep
import com.lionscare.app.data.repositories.baseresponse.Avatar
import com.lionscare.app.data.repositories.baseresponse.DateModel
import com.lionscare.app.data.repositories.baseresponse.Meta
import com.lionscare.app.data.repositories.baseresponse.QrValue

@Keep
data class PendingMemberResponse(
    var data: List<PendingMemberData>? = null,
    var has_morepage: Boolean? = null,
    var meta: Meta? = null,
    var msg: String? = null,
    var status: Boolean? = null,
    var status_code: String? = null,
    var total: Int? = null
)

@Keep
data class PendingMemberData(
    var date_created: DateModel? = null,
    var id: Int? = null,
    var status: String? = null,
    var type: String? = null,
    var user: User? = null
)

@Keep
data class User(
    var avatar: Avatar? = null,
    var date_registered: DateModel? = null,
    var firstname: String? = null,
    var id: String? = null,
    var lastname: String? = null,
    var middlename: String? = null,
    var name: String? = null,
    var qr_value: QrValue? = null,
    var qrcode: String? = null,
    var qrcode_value: String? = null
)


