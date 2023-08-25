package com.lionscare.app.data.repositories.baseresponse

import androidx.annotation.Keep

@Keep
data class UserModel(
    var id: String? = null,
    var firstname: String? = null,
    var middlename: String? = null,
    var lastname: String? = null,
    var email: String? = null,
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
    var date_registered: DateModel? = null
){
    fun getFullName() = "$firstname $lastname"
}
