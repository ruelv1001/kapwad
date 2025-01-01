package kapwad.reader.app.ui.register.viewmodel

import kapwad.reader.app.data.model.ErrorsData
import kapwad.reader.app.data.repositories.auth.response.LoginResponse
import kapwad.reader.app.data.repositories.registration.response.OnboardingScanQRResponse
import kapwad.reader.app.utils.PopupErrorState

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
