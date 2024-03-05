package dswd.ziacare.app.data.repositories.generalsetting.response


import androidx.annotation.Keep
import dswd.ziacare.app.data.repositories.baseresponse.Meta

@Keep
data class RequestAssistanceLOVResponse(
    var data: List<RequestAssistanceData>? = null,
    var meta: Meta? = null,
    var msg: String? = null,
    var status: Boolean? = false,
    var status_code: String? = null
)

@Keep
data class RequestAssistanceData(
    var code: String? = null,
    var name: String? = null
)