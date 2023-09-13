package com.lionscare.app.ui.onboarding.viewmodel

import com.lionscare.app.data.local.UserLocalData
import com.lionscare.app.utils.PopupErrorState

sealed class LoginViewState{
    object Loading : LoginViewState()
    data class Success(val message: String = "", val isCompleteProfile: Boolean = false) : LoginViewState()
    data class SuccessGetUserInfo(val userLocalData: UserLocalData = UserLocalData()) : LoginViewState()
    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : LoginViewState()
    data class InputError(val errorData: com.lionscare.app.data.model.ErrorsData? = null) : LoginViewState()
}
