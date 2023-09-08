package com.lionscare.app.data.repositories.assistance.response


import androidx.annotation.Keep
import com.lionscare.app.data.repositories.baseresponse.DateModel
import com.lionscare.app.data.repositories.baseresponse.Meta
import com.lionscare.app.data.repositories.member.response.User

@Keep
data class CreateAssistanceResponse(
    var data: CreateAssistanceData? = null,
    var meta: Meta? = null,
    var msg: String? = null,
    var status: Boolean? = null,
    var status_code: String? = null
)

@Keep
data class CreateAssistanceData(
    var amount: String? = null,
    var date_created: DateModel? = null,
    var id: Int? = null,
    var note: String? = null,
    var reason: String? = null,
    var reference_id: String? = null,
    var status: String? = null,
    var user: User? = null
)
