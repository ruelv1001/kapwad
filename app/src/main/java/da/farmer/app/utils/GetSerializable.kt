package da.farmer.app.utils

import android.content.Intent
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import java.io.Serializable

fun <T : Serializable?> getSerializable(intent: Intent, key: String, className: Class<T>): T {
    return if (Build.VERSION.SDK_INT >= 33)
        intent.getSerializableExtra(key, className)!!
    else
        intent.getSerializableExtra(key) as T
}

inline fun <reified T : Parcelable> Intent.getParcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
}

inline fun <reified T : Parcelable> Bundle.getParcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelable(key) as? T
}