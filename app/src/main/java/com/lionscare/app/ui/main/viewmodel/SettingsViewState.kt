package com.lionscare.app.ui.main.viewmodel

import com.lionscare.app.utils.PopupErrorState

sealed class SettingsViewState{
    object Loading : SettingsViewState()
    data class Success(val message: String = "") : SettingsViewState()
    data class SuccessGetUserInfo(val userLocalData: com.lionscare.app.data.local.UserLocalData = com.lionscare.app.data.local.UserLocalData()) : SettingsViewState()
    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : SettingsViewState()
    data class InputError(val errorData: com.lionscare.app.data.model.ErrorsData? = null) : SettingsViewState()
}
