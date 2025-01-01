package kapwad.reader.app.data.repositories.crops.request

import androidx.annotation.Keep
import java.io.File

@Keep
data class UploadImageRequest(
    var item_id: String,
    val type: String,
    val image: File? = null
)
