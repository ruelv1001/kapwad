package com.lionscare.app.utils

import android.app.Activity
import android.content.Context
import androidx.fragment.app.FragmentManager
import com.lionscare.app.R
import com.lionscare.app.security.AuthEncryptedDataManager
import com.lionscare.app.ui.onboarding.activity.LoginActivity
import com.lionscare.app.utils.dialog.CommonsErrorDialog

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
            CommonLogger.instance.sysLogE("EEE", errorCode)
            val intent = LoginActivity.getIntent(context)
            context.startActivity(intent)
            (context as Activity).finishAffinity()
            AuthEncryptedDataManager().clearUserInfo()
            //include this if app has local database
        }
    }
}