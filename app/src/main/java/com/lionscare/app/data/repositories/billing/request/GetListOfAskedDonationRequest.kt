package com.lionscare.app.data.repositories.billing.request

import androidx.annotation.Keep

@Keep
data class GetListOfAskedDonationRequest(
    var code: String,
    var page: Int? = 0,
    var per_page: Int? = 0,
    var keyword : String = ""
)
