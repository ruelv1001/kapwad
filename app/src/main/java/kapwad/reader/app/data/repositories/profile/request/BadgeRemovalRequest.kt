package kapwad.reader.app.data.repositories.profile.request

import androidx.annotation.Keep

@Keep
data class BadgeRemovalRequest(
    var reason : String? = null
)
