package kapwad.reader.app.utils

import android.app.Activity
import android.content.Context
import androidx.fragment.app.FragmentManager
import kapwad.reader.app.R
import kapwad.reader.app.security.AuthEncryptedDataManager
import kapwad.reader.app.ui.onboarding.activity.LoginActivity
import kapwad.reader.app.utils.dialog.CommonsErrorDialog

fun showPopupError(
    context: Context,
    fragmentManager: FragmentManager,
    errorCode: PopupErrorState,
    errorMessage: String
) {
    val message = when (errorCode) {
        PopupErrorState.NetworkError -> context.getString(R.string.common_network_msg)
        PopupErrorState.HttpError,
        PopupErrorState.SessionError -> errorMessage
        else -> context.getString(R.string.common_something_went_wrong_msg)
    }

    CommonsErrorDialog.openDialog(
        fragmentManager,
        message = message
    ){
        //return to splash screen an delete all user data
        if (errorCode == PopupErrorState.SessionError){
            val intent = LoginActivity.getIntent(context)
            context.startActivity(intent)
            (context as Activity).finishAffinity()
            AuthEncryptedDataManager().clearUserInfo()
            //include this if app has local database
        }
    }
}