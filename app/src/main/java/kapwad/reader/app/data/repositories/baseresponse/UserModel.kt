package kapwad.reader.app.data.repositories.baseresponse

import androidx.annotation.Keep
import java.io.Serializable

@Keep
data class UserModel(
    var mrid: String? = null,
    var date : String? = null,
    var firstname : QrValue? = null,
    var middlename : String ? = null,
    var lastname : Boolean? = false,
    var username : String? = null,
    var password : Avatar? = null,


    //var qr_value: String? = null
): Serializable {
    fun getFullName() = "$firstname ${middlename.orEmpty()} $lastname"
}


@Keep
data class QrValue(
    var type : String? = null,
    var value : String? = null,
    var signature : String? = null
) : Serializable
@Keep
data class Avatar(
    var filename : String? = null,
    var path : String? = null,
    var directory : String? = null,
    var full_path : String? = null,
    var thumb_path : String? = null
) : Serializable