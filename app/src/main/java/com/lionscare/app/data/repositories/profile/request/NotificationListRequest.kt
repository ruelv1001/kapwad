package com.lionscare.app.data.repositories.profile.request

import androidx.annotation.Keep

@Keep
data class NotificationListRequest(
    var group_id: String? = null,
    var notif_id: String? = null,
    var page: Int? = null,
    var per_page: Int? = null
)
