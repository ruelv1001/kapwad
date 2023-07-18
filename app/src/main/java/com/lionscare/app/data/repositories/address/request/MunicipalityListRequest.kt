package com.lionscare.app.data.repositories.address.request

data class MunicipalityListRequest(
    var reference: String,
    var type : String = "id"
)