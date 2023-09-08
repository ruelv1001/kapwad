package com.lionscare.app.ui.main.viewmodel

import com.lionscare.app.data.repositories.baseresponse.UserModel
import com.lionscare.app.utils.PopupErrorState

sealed class SettingsViewState{
    object Loading : SettingsViewState()
    data class Success(val message: String = "") : SettingsViewState()
    data class SuccessGetUserInfo(val message: String = "",val userModel: UserModel? = UserModel()) : SettingsViewState()
    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : SettingsViewState()
    data class InputError(val errorData: com.lionscare.app.data.model.ErrorsData? = null) : SettingsViewState()
}
