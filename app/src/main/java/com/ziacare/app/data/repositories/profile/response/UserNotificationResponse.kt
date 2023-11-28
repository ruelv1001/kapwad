package com.ziacare.app.data.repositories.profile.response

import androidx.annotation.Keep
import com.ziacare.app.data.repositories.baseresponse.DateModel
import com.ziacare.app.data.repositories.baseresponse.Meta

@Keep
data class UserNotificationResponse(
    val data: UserNotificationData? = null,
    var meta: Meta? = null,
    val msg: String? = null,
    val status: Boolean? = false,
    val status_code: String? = null
)

@Keep
data class UserNotificationData(
    val id: String? = null,
    val data: String? = null,
    val date_created: DateModel? = null
)
