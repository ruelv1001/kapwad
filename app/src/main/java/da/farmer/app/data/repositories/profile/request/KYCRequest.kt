package da.farmer.app.data.repositories.profile.request

import androidx.annotation.Keep
import java.io.File

@Keep
data class KYCRequest(
    var idType: String,
    val frontImageFile: File,
    val backImageFile: File? = null
)
