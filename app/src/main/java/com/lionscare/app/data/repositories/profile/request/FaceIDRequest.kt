package com.lionscare.app.data.repositories.profile.request

import androidx.annotation.Keep
import java.io.File

@Keep
data class FaceIDRequest(
    var right_image : File,
    var front_image : File,
    var left_image : File
)
