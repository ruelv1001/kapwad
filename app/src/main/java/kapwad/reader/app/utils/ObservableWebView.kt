package kapwad.reader.app.utils

import android.content.Context
import android.util.AttributeSet
import android.webkit.WebView

class ObservableWebView : WebView {
    var onScrollChangedCallback: OnScrollChangedCallback? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(
        context, attrs
    )

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context, attrs, defStyle
    )

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        onScrollChangedCallback?.onScroll(l, t, oldl, oldt)
    }

    interface OnScrollChangedCallback {
        fun onScroll(
            currentHorizontalScroll: Int,
            currentVerticalScroll: Int,
            oldHorizontalScroll: Int,
            oldcurrentVerticalScroll: Int
        )
    }
}