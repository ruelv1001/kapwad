package da.farmer.app.data.repositories.baseresponse

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class ImageModel(
    var directory: String? = null,
    var filename: String? = null,
    var full_path: String? = null,
    var path: String? = null,
    var thumb_path: String? = null,
) : Parcelable
@Keep
@Parcelize
data class AvatarModel(
    var filename: String? = null,
    var path: String? = null,
    var directory: String? = null,
    var full_path: String? = null,
    var thumb_path: String? = null
) : Parcelable
