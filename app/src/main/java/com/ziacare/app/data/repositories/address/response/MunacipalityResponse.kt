package com.ziacare.app.data.repositories.address.response

import androidx.annotation.Keep

@Keep
data class MunicipalityResponse(
    var data: List<MuniData>? = null,
    var msg: String? = null,
    var status: Boolean? = null,
    var status_code: String? = null,

    )

@Keep
data class MuniData(
    var province_id: Int? = null,
    var name: String? = null,
    var sku: String? = null,
    var citymun_id: Int? = null,
    var is_capital: Int? = null,
    var type: String? = null
)

