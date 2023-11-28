package com.ziacare.app.data.repositories.registration.response


import android.os.Parcelable
import androidx.annotation.Keep
import com.ziacare.app.data.repositories.baseresponse.DateModel
import com.ziacare.app.data.repositories.baseresponse.Meta
import com.ziacare.app.data.repositories.baseresponse.QrValue
import kotlinx.parcelize.Parcelize

@Keep
data class OnboardingScanQRResponse(
    var data: OnboardingScanQRData? = null,
    var meta: Meta? = null,
    var msg: String? = null,
    var status: Boolean? = false,
    var status_code: String? = null
)

@Keep
@Parcelize
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
) : Parcelable

