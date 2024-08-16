package da.farmer.app.data.repositories.group.response


import androidx.annotation.Keep
import da.farmer.app.data.repositories.baseresponse.Meta

@Keep
data class ImmediateFamilyResponse(
    var data: GroupListData? = null,
    var meta: Meta? = null,
    var msg: String? = null,
    var status: Boolean? = false,
    var status_code: String? = null
)
