package da.farmer.app.data.repositories.member.response


import androidx.annotation.Keep
import da.farmer.app.data.repositories.baseresponse.DateModel
import da.farmer.app.data.repositories.baseresponse.Meta

@Keep
data class JoinGroupResponse(
    var data: JoinGroupData? = null,
    var meta: Meta? = null,
    var msg: String? = null,
    var status: Boolean? = null,
    var status_code: String? = null
)

@Keep
data class JoinGroupData(
    var date_created: DateModel? = null,
    var group_id: String? = null,
    var id: Int? = null,
    var status: String? = null,
    var type: String? = null,
    var user: User? = null
)