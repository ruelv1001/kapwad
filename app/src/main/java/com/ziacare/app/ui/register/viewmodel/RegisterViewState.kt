package com.ziacare.app.ui.register.viewmodel

import com.ziacare.app.data.model.ErrorsData
import com.ziacare.app.data.repositories.auth.response.LoginResponse
import com.ziacare.app.data.repositories.registration.response.OnboardingScanQRResponse
import com.ziacare.app.utils.PopupErrorState

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
