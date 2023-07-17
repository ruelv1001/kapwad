package com.lionscare.app.ui.sample.viewmodel

import com.lionscare.app.utils.PopupErrorState

sealed class LoginViewState{
    object Loading : LoginViewState()
    data class Success(val message: String = "") : LoginViewState()
    data class SuccessGetUserInfo(val userLocalData: com.lionscare.app.data.local.UserLocalData = com.lionscare.app.data.local.UserLocalData()) : LoginViewState()
    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : LoginViewState()
    data class InputError(val errorData: com.lionscare.app.data.model.ErrorsData? = null) : LoginViewState()
}
