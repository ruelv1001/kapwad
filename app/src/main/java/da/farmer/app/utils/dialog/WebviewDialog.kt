package da.farmer.app.utils.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.net.http.SslError
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.os.bundleOf
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import da.farmer.app.R
import da.farmer.app.databinding.DialogWebviewBinding
import da.farmer.app.utils.ObservableWebView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import da.farmer.app.utils.CommonLogger


class WebviewDialog private constructor() : BottomSheetDialogFragment() {

    private var viewBinding: DialogWebviewBinding? = null
    private val url by lazy { arguments?.getString(EXTRA_URL).orEmpty() }
    private var currentWebScrollY = 0
    private var listener: WebViewListener? = null
    private var javaScriptInterface: JavaScriptInterface? = null


    override fun onStart() {
        super.onStart()
        val bottomSheet =
            dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        val height = (resources.displayMetrics.heightPixels * .95).toInt()
        bottomSheet?.layoutParams?.height = height
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(
            R.layout.dialog_webview,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding = DialogWebviewBinding.bind(view)

        initWebView()
        setWebClient()
        loadUrl(url)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return (super.onCreateDialog(savedInstanceState) as BottomSheetDialog).apply {
            val height = (resources.displayMetrics.heightPixels * .95).toInt()
            behavior.peekHeight = height
            behavior.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {

                }

                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_DRAGGING && currentWebScrollY > 0) {
                        behavior.setState(BottomSheetBehavior.STATE_EXPANDED)
                    } else if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        dismiss()
                    }
                }
            })
        }
    }

    interface WebViewListener {
        fun onDissmiss()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        viewBinding?.apply {

            webView.settings.javaScriptEnabled = true
            webView.settings.loadWithOverviewMode = true
            webView.settings.useWideViewPort = true
            webView.settings.domStorageEnabled = true
            javaScriptInterface = JavaScriptInterface(requireActivity())
            javaScriptInterface?.let {
                webView.addJavascriptInterface(it, "JSInterface")
            }
            webView.webViewClient = object : WebViewClient() {
                @SuppressLint("WebViewClientOnReceivedSslError")
                override
                fun onReceivedSslError(
                    view: WebView?,
                    handler: SslErrorHandler?,
                    error: SslError?,
                ) {
                    handleOnReceiveErrorSsl(handler)
                }
            }
            webView.onScrollChangedCallback = object : ObservableWebView.OnScrollChangedCallback {
                override fun onScroll(
                    currentHorizontalScroll: Int,
                    currentVerticalScroll: Int,
                    oldHorizontalScroll: Int,
                    oldcurrentVerticalScroll: Int
                ) {
                    currentWebScrollY = currentVerticalScroll
                }

            }
        }
    }

    private fun setWebClient() {
        viewBinding?.apply {

            webView.webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    progressBar.isVisible = newProgress < 100
                    webView.isInvisible = newProgress < 100
                    progressBar.progress = newProgress
                    super.onProgressChanged(view, newProgress)
                }

                override fun onReceivedTitle(view: WebView?, title: String?) {
//                    CommonLogger.devLog("huhua","title : ${title.toString()}")
                    if (title.toString() == "CONFIMATION PAGE"){
                        listener?.onDissmiss()
                    }
                    super.onReceivedTitle(view, title)
                }
            }

            dialog?.setOnCancelListener { dialog ->
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    dialog.dismiss()
                }
            }

            dialog?.setOnKeyListener { _, keyCode, _ ->
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (webView.canGoBack()) {
                        webView.goBack()
                    } else {
                        dialog?.dismiss()
                    }
                }
                true
            }
        }
    }

    private fun loadUrl(pageUrl: String) {
        viewBinding?.webView?.loadUrl(pageUrl.toSecuredUri())
    }

    private fun handleOnReceiveErrorSsl(handler: SslErrorHandler?) {

    }

    private fun String.toSecuredUri(): String {
        return Uri.parse(this).buildUpon().scheme("https").toString()

    }

    inner class JavaScriptInterface(internal var context: Context) {
        @JavascriptInterface
        fun closeWindow() {
            listener?.onDissmiss()
            dismiss()
        }
    }

    companion object {
        private val TAG = WebviewDialog::class.java.simpleName
        private const val EXTRA_URL = "EXTRA_URL"

        fun openDialog(
            fragmentManager: FragmentManager,
            url: String,
            listener: WebViewListener? = null,
        ) {
            WebviewDialog().apply {
                this.listener = listener
                arguments = bundleOf(EXTRA_URL to url)
                this
            }
                .show(fragmentManager, TAG)
        }
    }
}