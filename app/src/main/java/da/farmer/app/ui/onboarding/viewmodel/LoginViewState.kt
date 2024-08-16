package da.farmer.app.ui.onboarding.viewmodel

import da.farmer.app.data.local.UserLocalData
import da.farmer.app.utils.PopupErrorState

sealed class LoginViewState{
    object Loading : LoginViewState()
    data class Success(val message: String = "", val isCompleteProfile: Boolean = false) : LoginViewState()
    data class SuccessForgotPassword(val message: String = "") : LoginViewState()
    data class SuccessGetUserInfo(val userLocalData: UserLocalData = UserLocalData()) : LoginViewState()
    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : LoginViewState()
    data class InputError(val errorData: da.farmer.app.data.model.ErrorsData? = null) : LoginViewState()
}
