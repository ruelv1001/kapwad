package com.lionscare.app.ui.register.viewmodel

import com.lionscare.app.data.model.ErrorsData
import com.lionscare.app.data.repositories.auth.response.LoginResponse
import com.lionscare.app.data.repositories.registration.response.OnboardingScanQRResponse
import com.lionscare.app.utils.PopupErrorState

sealed class RegisterViewState {
    object Loading : RegisterViewState()
    data class Success(val message: String = "") : RegisterViewState()
    data class SuccessScanQR(val message: String = "", val onboardingScanQRResponse: OnboardingScanQRResponse) : RegisterViewState()
    data class SuccessReg(val message: String = "") : RegisterViewState()
    data class SuccessValidateAndSetPassword(val message: String = "",val loginResponse: LoginResponse) : RegisterViewState()
    data class SuccessProfileUpdate(val message: String = "") : RegisterViewState()
    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : RegisterViewState()
    data class InputError(val errorData: ErrorsData? = null) : RegisterViewState()
}
