package da.farmer.app.data.repositories.profile.response


import androidx.annotation.Keep
import da.farmer.app.data.repositories.baseresponse.DateModel

@Keep
data class BadgeStatusResponse(
    val `data`: BadgeStatus? = null,
    val msg: String? = null,
    val status: Boolean? = false,
    val status_code: String? = null
)
@Keep
data class BadgeStatus(
    val activated_date: DateModel? = null,
    val badge_type: String? = null,
    val id: Long? = 0,
    val remarks: String? = null,
    val status: String? = null,
    val submitted_date: DateModel? = null,
    val has_badge_cancellation_request: Boolean? = false
)
