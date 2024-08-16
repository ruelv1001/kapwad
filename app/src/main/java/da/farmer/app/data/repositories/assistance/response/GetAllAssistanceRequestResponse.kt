package da.farmer.app.data.repositories.assistance.response


import androidx.annotation.Keep
import da.farmer.app.data.repositories.baseresponse.Meta

@Keep
data class GetAllAssistanceRequestResponse(
    var data: List<CreateAssistanceData>? = null,
    var has_morepages: Boolean? = null,
    var meta: Meta? = null,
    var msg: String? = null,
    var status: Boolean? = null,
    var status_code: String? = null,
    var total: Int? = null
)