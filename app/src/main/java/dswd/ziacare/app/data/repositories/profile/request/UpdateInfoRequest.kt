package dswd.ziacare.app.data.repositories.profile.request

import androidx.annotation.Keep

@Keep
data class UpdateInfoRequest(
    var province_sku: String,
    var province_name: String,
    var city_sku: String,
    var city_name: String,
    var brgy_sku: String,
    var brgy_name: String,
    var street_name: String,
    var zipcode: String,
    var firstname: String,
    var lastname: String,
    var middlename: String,
    var email: String? = null, //could be requested in diff screen,
    var birthdate: String,

    )
