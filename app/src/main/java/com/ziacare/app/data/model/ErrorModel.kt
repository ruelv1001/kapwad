package com.ziacare.app.data.model

import androidx.annotation.Keep

@Keep
data class ErrorModel(
    val msg: String? = null,
    val status: Boolean? = false,
    val status_code: String? = null,
    val has_requirements: Boolean? = false,
    var errors: ErrorsData? = null
)

@Keep
data class  ErrorsData(
    var email: List<String>? = null,
    var username: List<String>? = null,
    var password: List<String>? = null,
    var birthdate: List<String>? = null,
    var zipcode: List<String>? = null,
    var current_password: List<String>? = null,
    var password_confirmation: List<String>? = null,
    var firstname: List<String>? = null,
    var lastname: List<String>? = null,
    var middlename: List<String>? = null,
    var phone_number: List<String>? = null,
    var name: List<String>? = null,
    var image: List<String>? = null,
    var type : List<String>? = null,
    var otp: List<String>? = null,
    var province_name: List<String>? = null,
    var city_name: List<String>? = null,
    var brgy_name: List<String>? = null,
    var desc: List<String>? = null,
    var street_name: List<String>? = null,
    var group_name: List<String>? = null,
    var group_type: List<String>? = null,
    var group_privacy: List<String>? = null,
    var group_approval: List<String>? = null,
    var group_passcode: List<String>? = null,
    var passcode: List<String>? = null,
    var amount: List<String>? = null,
    var lc_location_id: List<String>? = null,
    var lc_zone_id: List<String>? = null,
    var doc1: List<String>? = null,
    var doc2: List<String>? = null

)
