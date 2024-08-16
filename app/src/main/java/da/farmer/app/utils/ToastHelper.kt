package da.farmer.app.utils

import android.content.Context
import com.mhadikz.toaster.Toaster

/**
 * Show toast info
 *
 * @param context
 * @param title Title of toast, default to info
 * @param description message
 */
fun showToastInfo(context: Context, title : String = "Info", description : String){
    Toaster.Builder(context)
        .setTitle(title)
        .setDescription(description)
        .setDuration(Toaster.LENGTH_LONG)
        .setStatus(Toaster.Status.INFO)
        .show();
}

/**
 * Show toast success
 *
 * @param context
 * @param title Title of toast, default to Success
 * @param description message
 */
fun showToastSuccess(context: Context, title : String = "Success", description : String){
    Toaster.Builder(context)
        .setTitle(title)
        .setDescription(description)
        .setDuration(Toaster.LENGTH_SHORT)
        .setStatus(Toaster.Status.SUCCESS)
        .show();
}

/**
 * Show toast warning
 *
 * @param context
 * @param title Title of toast, default to Warning
 * @param description message
 */
fun showToastWarning(context: Context, title : String = "Warning", description : String){
    Toaster.Builder(context)
        .setTitle(title)
        .setDescription(description)
        .setDuration(Toaster.LENGTH_LONG)
        .setStatus(Toaster.Status.WARNING)
        .show();
}

/**
 * Show toast error
 *
 * @param context
 * @param title Title of toast, default to Error
 * @param description message
 */
fun showToastError(context: Context, title : String = "Error", description : String){
    Toaster.Builder(context)
        .setTitle(title)
        .setDescription(description)
        .setDuration(Toaster.LENGTH_LONG)
        .setStatus(Toaster.Status.ERROR)
        .show();
}