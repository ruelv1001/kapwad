package com.lionscare.app.utils

import android.content.Context
import androidx.fragment.app.FragmentManager
import com.lionscare.app.R
import com.lionscare.app.utils.dialog.CommonsErrorDialog

fun showPopupError(
    context: Context,
    fragmentManager: FragmentManager,
    errorCode: PopupErrorState,
    errorMessage: String
) {
    val message = when (errorCode) {
        PopupErrorState.NetworkError -> context.getString(R.string.common_network_msg)
        PopupErrorState.HttpError -> errorMessage
        else -> context.getString(R.string.common_something_went_wrong_msg)
    }

    CommonsErrorDialog.openDialog(
        fragmentManager,
        message = message
    )
}