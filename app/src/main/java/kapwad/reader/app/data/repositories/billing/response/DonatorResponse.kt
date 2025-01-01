package kapwad.reader.app.data.repositories.billing.response

import androidx.annotation.Keep
import kapwad.reader.app.data.repositories.baseresponse.DateModel
import kapwad.reader.app.data.repositories.baseresponse.UserModel

//TODO TEMPORARY
@Keep
data class DonatorResponse(
    val `data`: DonatorData? = null,
    val msg: String? = null,
    val status: Boolean? = false,
    val status_code: String? = null
)

@Keep
data class DonatorData(
    val id : Long? = 0,
    val amount : String? = null,
    val user : UserModel? = null,
    val date : DateModel? = null
)