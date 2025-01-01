package kapwad.reader.app.ui.onboarding.viewmodel

import kapwad.reader.app.data.local.UserLocalData
import kapwad.reader.app.utils.PopupErrorState

sealed class LoginViewState{
    object Loading : LoginViewState()
    data class Success(val message: String = "", val isCompleteProfile: Boolean = false) : LoginViewState()
    data class SuccessForgotPassword(val message: String = "") : LoginViewState()
    data class SuccessGetUserInfo(val userLocalData: UserLocalData = UserLocalData()) : LoginViewState()
    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : LoginViewState()
    data class InputError(val errorData: kapwad.reader.app.data.model.ErrorsData? = null) : LoginViewState()
}
