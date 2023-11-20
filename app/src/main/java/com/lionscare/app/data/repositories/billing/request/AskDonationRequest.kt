package com.lionscare.app.data.repositories.billing.request

import androidx.annotation.Keep

@Keep
data class AskDonationRequest(
    var code : String,
    var reference_id : List<String>? = null,
)
