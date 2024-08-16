package da.farmer.app.data.repositories.profile.response

import androidx.annotation.Keep
import da.farmer.app.data.repositories.baseresponse.DateModel

@Keep
data class BadgeRemovalStatusResponse(
    val `data`: BadgeRemovalStatus? = null,
    val msg: String? = null,
    val status: Boolean? = false,
    val status_code: String? = null
)

@Keep
data class BadgeRemovalStatus(
    val id: Long? = 0,
    val status : String? = null,
    val reason : String? = null,
    val requested_at : DateModel? = null
)
