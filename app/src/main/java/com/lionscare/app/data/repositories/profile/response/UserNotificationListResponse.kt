package com.lionscare.app.data.repositories.profile.response

import androidx.annotation.Keep
import com.lionscare.app.data.repositories.baseresponse.Meta

@Keep
data class UserNotificationListResponse(
    val data: List<UserNotificationData>? = null,
    val msg: String? = null,
    var has_morepages: Boolean? = null,
    var meta: Meta? = null,
    val status: Boolean? = false,
    val status_code: String? = null,
    var total: Int? = null
)