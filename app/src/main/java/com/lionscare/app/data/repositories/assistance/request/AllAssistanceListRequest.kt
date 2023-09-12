package com.lionscare.app.data.repositories.assistance.request

data class AllAssistanceListRequest (
    var filter: List<String>,
    var group_id: String,
    var page: Int,
    var per_page: Int
)