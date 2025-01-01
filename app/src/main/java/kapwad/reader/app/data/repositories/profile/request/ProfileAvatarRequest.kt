package kapwad.reader.app.data.repositories.profile.request

import androidx.annotation.Keep
import java.io.File

@Keep
data class ProfileAvatarRequest(
    var image: File
)
