package com.lionscare.app.data.repositories.baseresponse

import androidx.annotation.Keep

@Keep
data class GeneralResponse(
    val msg: String? = null,
    val status: Boolean? = false,
    val status_code: String? = null,
    val web_checkout: String? = null,
    var hint: String? = null
)


@Keep
data class Meta(
    var authors: List<String>? = null,
    var copyright: String? = null,
    var jsonapi: Jsonapi? = null
)

@Keep
data class Jsonapi(
    var build: String? = null,
    var version: String? = null
)