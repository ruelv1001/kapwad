package kapwad.reader.app.security

import android.content.Context
import android.content.SharedPreferences

import kapwad.reader.app.BuildConfig
import kapwad.reader.app.base.CommonsLib

class SharedPref {

    private val sharedPreferences = getSharedPreferences()

    fun getLocalUrl(): String? {
        return sharedPreferences.getString("localUrl", BuildConfig.BASE_URL)
    }

    fun setLocalUrl(localUrl: String) {
        sharedPreferences.edit().putString("localUrl", localUrl).apply()
    }

    fun clearLocalUrl() {
        sharedPreferences.edit().remove("localUrl").apply()
    }

    private fun getSharedPreferences(): SharedPreferences {
        val context = CommonsLib.context!!
        return context.getSharedPreferences("LocalServerSharedPref", Context.MODE_PRIVATE)
    }
}