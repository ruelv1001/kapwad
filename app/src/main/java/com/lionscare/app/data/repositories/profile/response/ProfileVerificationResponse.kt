package com.lionscare.app.data.repositories.profile.response


import androidx.annotation.Keep
import com.lionscare.app.data.repositories.baseresponse.DateModel

@Keep
data class ProfileVerificationResponse(
    val `data`: Verification? = null,
    val msg: String? = null,
    val status: Boolean? = false,
    val status_code: String? = null
)
@Keep
data class Verification(
    val address_remarks: String? = null,
    val address_status: String? = null,
    val address_submitted_date: DateModel? = null,
    val address_type: String? = null,
    val address_verified_date: DateModel? = null,
    val id: Int? = 0,
    val id_remarks: String? = null,
    val id_status: String? = null,
    val id_submitted_date: DateModel? = null,
    val id_type: String? = null,
    val id_verified_date: DateModel? = null
)
