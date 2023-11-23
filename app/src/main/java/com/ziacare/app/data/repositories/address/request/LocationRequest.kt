package com.ziacare.app.data.repositories.address.request


import androidx.annotation.Keep

@Keep
data class LocationRequest(
    var region_id: String? = null,
    var zone_id: String? = null
)