package com.lionscare.app.data.repositories.group.response


import androidx.annotation.Keep
import com.lionscare.app.data.repositories.baseresponse.DateModel
import com.lionscare.app.data.repositories.baseresponse.Meta
import com.lionscare.app.data.repositories.baseresponse.QrValue

@Keep
data class PendingGroupRequestsListResponse(
    var data: List<PendingGroupRequestData>? = null,
    var meta: Meta? = null,
    var msg: String? = null,
    var status: Boolean? = null,
    var status_code: String? = null
)

@Keep
data class PendingGroupRequestData(
    var date_created: DateModel? = null,
    var group: Group? = null,
    var group_id: String? = null,
    var id: Int? = null,
    var type: String? = null
)


@Keep
data class Group(
    var code: String? = null,
    var date_created: DateModel? = null,
    var id: String? = null,
    var member_count: Int? = null,
    var name: String? = null,
    var privacy: String? = null,
    var qr_value: QrValue? = null,
    var qrcode: String? = null,
    var qrcode_value: String? = null,
    var type: String? = null
)
