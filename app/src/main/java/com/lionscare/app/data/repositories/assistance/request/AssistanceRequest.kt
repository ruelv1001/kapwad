package com.lionscare.app.data.repositories.assistance.request


import androidx.annotation.Keep

@Keep
data class AssistanceRequest(
    var filter: List<String>? = null,
    var group_id: String? = null,
    var per_page: Int? = null,
    var reference_id: String? = null,
    var request_id: String? = null,
    var remarks: String? = null
)