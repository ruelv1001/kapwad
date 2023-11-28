package com.ziacare.app.base

import android.content.Context

object CommonsLib {
    var context: Context? = null
        private set

    var isDevCommonLoggerEnable = true
    var isSystemCommonLoggerEnable = true

    fun init(context: Context) { com.ziacare.app.base.CommonsLib.context = context }
}