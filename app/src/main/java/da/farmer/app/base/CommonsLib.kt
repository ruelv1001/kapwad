package da.farmer.app.base

import android.content.Context

object CommonsLib {
    var context: Context? = null
        private set

    var isDevCommonLoggerEnable = true
    var isSystemCommonLoggerEnable = true

    fun init(context: Context) { da.farmer.app.base.CommonsLib.context = context }
}