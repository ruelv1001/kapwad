package dswd.ziacare.app.data.repositories.profile.request

import androidx.annotation.Keep
import java.io.File

@Keep
data class BadgeRequest(
    var doc1 : File,
    var doc2 : File,
    var type : String
)
