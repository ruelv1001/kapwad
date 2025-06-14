package kapwad.reader.app.data.repositories.profile.response

import androidx.annotation.Keep

@Keep
data class BadgeResponse(
    val msg : String? = null,
    val status : Boolean? = false,
    val status_code : String? = null,
)
