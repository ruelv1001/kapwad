package kapwad.reader.app.utils

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import com.emrekotun.toast.CpmToast
import com.emrekotun.toast.CpmToast.Companion.toastInfo
import kapwad.reader.app.R

fun Activity.copyToClipboard(text: CharSequence){
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("label",text)
    clipboard.setPrimaryClip(clip)
    this.toastInfo(getString(R.string.copied_to_clipboard), CpmToast.LONG_DURATION)
}