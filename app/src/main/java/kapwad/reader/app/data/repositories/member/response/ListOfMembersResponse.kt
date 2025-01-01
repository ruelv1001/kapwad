package kapwad.reader.app.data.repositories.member.response


import androidx.annotation.Keep
import kapwad.reader.app.data.repositories.baseresponse.DateModel
import kapwad.reader.app.data.repositories.baseresponse.Meta

@Keep
data class ListOfMembersResponse(
    var data: List<MemberListData>? = null,
    var has_morepage: Boolean? = null,
    var meta: Meta? = null,
    var msg: String? = null,
    var status: Boolean? = null,
    var status_code: String? = null,
    var total: Int? = null
)

@Keep
data class MemberListData(
    var date_joined: DateModel? = null,
    var id: Int? = null,
    var role: String? = null,
    var user: User? = null
)



