package com.lionscare.app.data.repositories.profile.request

import java.io.File

data class KYCRequest(
    var idType: String,
    val frontImageFile: File,
    val backImageFile: File? = null
)
