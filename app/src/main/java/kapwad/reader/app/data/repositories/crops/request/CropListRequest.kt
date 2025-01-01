package kapwad.reader.app.data.repositories.crops.request


import androidx.annotation.Keep

@Keep
class CropListRequest(
    val per_page: String? = null,
    var page: String? = null,

)