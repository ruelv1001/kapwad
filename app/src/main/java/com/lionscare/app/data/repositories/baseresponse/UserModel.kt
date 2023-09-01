package com.lionscare.app.data.repositories.baseresponse

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Keep
@Parcelize
data class UserModel(
    var id: String? = null,
    var qrcode: String? = null,
    var qr_value: QRValueModel? = null,
    var qrcode_value: String? = null,
    var name: String? = null,
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
    var date_registered: DateModel? = null,
    var avatar: AvatarModel? = null
): Parcelable

@Keep
@Parcelize
data class QRValueModel(
    var type: String? = null,
    var value: String? = null,
    var signature: String? = null
) : Parcelable
