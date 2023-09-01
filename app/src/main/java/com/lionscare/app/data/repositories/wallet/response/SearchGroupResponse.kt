package com.lionscare.app.data.repositories.wallet.response


import androidx.annotation.Keep
import com.lionscare.app.data.repositories.group.response.GroupData

@Keep
data class SearchGroupResponse(
    val data: List<GroupData>? = null,
    val msg: String? = null,
    val status: Boolean? = null,
    val status_code: String? = null
)