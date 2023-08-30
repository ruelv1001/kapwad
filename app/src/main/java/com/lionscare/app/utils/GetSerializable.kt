package com.lionscare.app.utils

import android.content.Intent
import android.os.Build
import java.io.Serializable

fun <T : Serializable?> getSerializable(intent: Intent, key: String, className: Class<T>): T {
    return if (Build.VERSION.SDK_INT >= 33)
        intent.getSerializableExtra(key, className)!!
    else
        intent.getSerializableExtra(key) as T
}