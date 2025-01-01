package kapwad.reader.app.data.repositories.geotagging.request


import androidx.annotation.Keep
import java.io.File

@Keep
class GeotaggingUploadRequest(
    var remarks: String,
    val land_area: String,
    val image: File? = null
)