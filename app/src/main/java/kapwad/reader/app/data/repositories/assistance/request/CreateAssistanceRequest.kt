package kapwad.reader.app.data.repositories.assistance.request


import androidx.annotation.Keep

@Keep
data class CreateAssistanceRequest(
    var amount: String?,
    var group_id: String?,
    var reason: String?,
    var remarks: String?
)