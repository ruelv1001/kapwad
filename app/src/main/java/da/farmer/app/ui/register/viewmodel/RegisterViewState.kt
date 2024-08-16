package da.farmer.app.ui.register.viewmodel

import da.farmer.app.data.model.ErrorsData
import da.farmer.app.data.repositories.auth.response.LoginResponse
import da.farmer.app.data.repositories.registration.response.OnboardingScanQRResponse
import da.farmer.app.utils.PopupErrorState

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
