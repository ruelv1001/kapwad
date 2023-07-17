package com.lionscare.app.base

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaseApplication : Application() {

    companion object {
        var instance: com.lionscare.app.base.BaseApplication? = null
    }

    override fun onCreate() {
        super.onCreate()
        com.lionscare.app.base.BaseApplication.Companion.instance = this
        initCommonsLib()
    }

    private fun initCommonsLib() {
        com.lionscare.app.base.CommonsLib.init(this)
    }
}