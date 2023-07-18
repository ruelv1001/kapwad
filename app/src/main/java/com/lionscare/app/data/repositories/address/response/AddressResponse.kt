package com.lionscare.app.data.repositories.address.response

data class AddressResponse(
    var data: List<AddressData>? = null,
    var msg: String? = null,
    var status: Boolean? = null,
    var status_code: String? = null,

    )

data class AddressData(
    var province_id: Int? = null,
    var name: String? = null,
    var sku: String? = null,
    var citymun_id: Int? = null,
    var is_capital: Int? = null,
    var region_code: Int? = null,
    var province_code: Int? = null,
    var citymun_code: Int? = null,
    var type: String? = null,
    var code: String? = null,
    var zipcode: String? = null
)

