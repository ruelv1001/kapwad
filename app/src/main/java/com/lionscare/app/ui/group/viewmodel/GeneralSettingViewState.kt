package com.lionscare.app.ui.group.viewmodel

import com.lionscare.app.data.repositories.generalsetting.response.RequestAssistanceLOVResponse
import com.lionscare.app.utils.PopupErrorState

sealed class GeneralSettingViewState {

    object Loading : GeneralSettingViewState()
    data class Success(val requestAssistanceLOVResponse: RequestAssistanceLOVResponse? = null) : GeneralSettingViewState()
    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : GeneralSettingViewState()

}