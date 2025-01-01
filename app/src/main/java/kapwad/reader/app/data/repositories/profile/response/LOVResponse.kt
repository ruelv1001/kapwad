package kapwad.reader.app.data.repositories.profile.response

import androidx.annotation.Keep
@Keep
data class LOVResponse(
    val data: List<LOVData>? = null,
    val msg: String? = null,
    val status: Boolean? = false,
    val status_code: String? = null
)

@Keep
data class LOVData(
    val code: String? = null,
    val name: String? = null,
    val value: String? =null
)
