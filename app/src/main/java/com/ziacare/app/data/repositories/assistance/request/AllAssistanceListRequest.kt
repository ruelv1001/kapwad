package com.ziacare.app.data.repositories.assistance.request

import androidx.annotation.Keep

@Keep
data class AllAssistanceListRequest (
    var filter: List<String>,
    var group_id: String,
    var page: Int,
    var per_page: Int
)