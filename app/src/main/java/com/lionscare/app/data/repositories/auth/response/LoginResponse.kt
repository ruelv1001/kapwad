package com.lionscare.app.data.repositories.auth.response

import com.lionscare.app.data.repositories.baseresponse.UserModel

data class LoginResponse(
    val data: UserModel? = null,
    val msg: String? = null,
    val status: Boolean? = false,
    val status_code: String? = null,
    val token: String? = null,
    val token_type: String? = null
)