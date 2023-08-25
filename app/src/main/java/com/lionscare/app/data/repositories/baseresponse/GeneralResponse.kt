package com.lionscare.app.data.repositories.baseresponse

import androidx.annotation.Keep

@Keep
data class GeneralResponse(
    val msg: String?,
    val status: Boolean?,
    val status_code: String?
)


@Keep
data class Meta(
    var authors: List<String?>?,
    var copyright: String?,
    var jsonapi: Jsonapi?
)

@Keep
data class Jsonapi(
    var build: String?,
    var version: String?
)