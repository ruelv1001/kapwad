package kapwad.reader.app.data.repositories.auth.response


import androidx.annotation.Keep

@Keep
data class ReasonsResponse(
    val data: List<ReasonsData>? = null,
    val msg: String? = null,
    val status: Boolean? = false,
    val status_code: String? = null
)

@Keep
data class ReasonsData(
    val id: Int? = 0,
    val reason: String? = null,
    var isSelected: Boolean? = false
)