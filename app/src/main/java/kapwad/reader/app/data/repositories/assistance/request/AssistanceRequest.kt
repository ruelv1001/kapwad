package kapwad.reader.app.data.repositories.assistance.request


import androidx.annotation.Keep

@Keep
data class AssistanceRequest(
    var group_id: String? = null,
    var page: Int? = null,
    var per_page: Int? = null,
    var reference_id: String? = null,
    var remarks: String? = null
)