package com.lionscare.app.ui.settings.viewmodel

import com.lionscare.app.data.local.UserLocalData
import com.lionscare.app.data.model.ErrorsData
import com.lionscare.app.data.repositories.baseresponse.UserModel
import com.lionscare.app.ui.onboarding.viewmodel.LoginViewState
import com.lionscare.app.utils.PopupErrorState

sealed class ProfileViewState {
    object Loading : ProfileViewState()
    data class Success(val message: String = "") : ProfileViewState()
    data class SuccessGetUserInfo(val message: String = "",val userModel: UserModel? = UserModel()) : ProfileViewState()
    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : ProfileViewState()
    data class InputError(val errorData: ErrorsData? = null) : ProfileViewState()
}
