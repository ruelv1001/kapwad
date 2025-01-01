package kapwad.reader.app.data.repositories.group.request


import androidx.annotation.Keep

@Keep
data class CreateGroupRequest(
    var group_id: String? = null,
    var name: String? = null,
    var type: String? = null,
    var privacy: String? = null,
    var passcode: String? = null,
    var with_approval: Int? = 0
)