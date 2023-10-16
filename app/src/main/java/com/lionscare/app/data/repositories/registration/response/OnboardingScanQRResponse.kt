package com.lionscare.app.data.repositories.registration.response


import androidx.annotation.Keep
import com.lionscare.app.data.repositories.baseresponse.DateModel
import com.lionscare.app.data.repositories.baseresponse.Meta
import com.lionscare.app.data.repositories.baseresponse.QrValue

@Keep
data class OnboardingScanQRResponse(
    var data: OnboardingScanQRData? = null,
    var meta: Meta? = null,
    var msg: String? = null,
    var status: Boolean? = false,
    var status_code: String? = null
)

@Keep
data class OnboardingScanQRData(
    var birthdate: DateModel? = null,
    var date_registered: DateModel? = null,
    var firstname: String? = null,
    var id: String? = null,
    var is_complete_profile: Boolean? = null,
    var lastname: String? = null,
    var middlename: String? = null,
    var name: String? = null,
    var phone_country_code: String? = null,
    var phone_country_iso: String? = null,
    var phone_number: String? = null,
    var phone_verified: Boolean? = false,
    var qr_value: QrValue? = null,
    var qrcode: String? = null,
    var qrcode_value: String? = null
)

