package kapwad.reader.app.data.repositories.baseresponse

import androidx.annotation.Keep
import java.io.Serializable

@Keep
data class UserModel(
    var id: String? = null,
    var qrcode : String? = null,
    var qr_value : QrValue? = null,
    var qrcode_value : String ? = null,
    var is_complete_profile : Boolean? = false,
    var name : String? = null,
    var avatar : Avatar? = null,
    var firstname: String? = null,
    var middlename: String? = null,
    var lastname: String? = null,
    var email: String? = null,
    var email_verified : Boolean? = false,
    var phone_number: String? = null,
    var address: String? = null,
    var province_name: String? = null,
    var province_sku: String? = null,
    var city_name: String? = null,
    var city_code: String? = null,
    var brgy_name: String? = null,
    var brgy_code: String? = null,
    var street_name: String? = null,
    var zipcode: String? = null,
    var birthdate : DateModel? = null,
    var date_registered: DateModel? = null,
    var badge_type : String? = null,
    var badge_type_issued_at: DateModel? = null,
    var kyc_status : String? = "completed"
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