package dswd.ziacare.app.base

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaseApplication : Application() {

    companion object {
        var instance: BaseApplication? = null
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        initCommonsLib()
        FirebaseApp.initializeApp(this)
    }

    private fun initCommonsLib() {
        CommonsLib.init(this)
    }
}