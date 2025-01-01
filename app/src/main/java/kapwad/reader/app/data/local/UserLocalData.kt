package kapwad.reader.app.data.local

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity(tableName = "users")
data class UserLocalData(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val user_id: String? = null,
    var qrcode: String? = null,

//    val qr_value: QRValueModel? = null,
    val qr_value_type: String? = null,
    val qr_value_value: String? = null,
    val qr_value_signature: String? = null,

    val qrcode_value: String? = null,
    val name: String? = null,
    val firstname: String? = null,
    val middlename: String? = null,
    val lastname: String? = null,
    val email: String? = null,
    val phone_number: String? = null,
    val address: String? = null,
    val province_name: String? = null,
    val province_sku: String? = null,
    val city_name: String? = null,
    val city_code: String? = null,
    val brgy_name: String? = null,
    val brgy_code: String? = null,
    val street_name: String? = null,
    val zipcode: String? = null,

//    val date_registered: DateModel? = null,
    val date_registered_date_db: String? = null,
    val date_registered_date_only: String? = null,
    val date_registered_time_passed: String? = null,
    val date_registered_timestamp: String? = null,
    val date_registered_iso_format: String? = null,
    val date_registered_month_year: String? = null,

//    val avatar: AvatarModel? = null,
    val avatar_filename: String? = null,
    val avatar_path: String? = null,
    val avatar_directory: String? = null,
    val avatar_full_path: String? = null,
    val avatar_thumb_path: String? = null,

    var badge_type : String? = null,
    val badge_type_date_db: String? = null,
    val badge_type_date_only: String? = null,
    val badge_type_time_passed: String? = null,
    val badge_type_timestamp: String? = null,
    val badge_type_iso_format: String? = null,
    val badge_type_month_year: String? = null,
    var kyc_status : String? = null,

    val access_token: String? = null
){
    fun getFullName() = "$firstname $middlename $lastname"
}
