package da.farmer.app.data.repositories.profile.response

import androidx.annotation.Keep

@Keep
data class KYCGeneralResponse(
    val code: String?,
    val msg: String?,
    val status: Boolean?,
    val status_code: String?,
)
