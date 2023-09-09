package com.lionscare.app.data.repositories.address.request

import androidx.annotation.Keep

@Keep
data class RegionRequest(
    var hint : Boolean? = true
)