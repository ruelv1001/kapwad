package com.ziacare.app.ui.group.viewmodel

import com.ziacare.app.data.repositories.generalsetting.response.RequestAssistanceLOVResponse
import com.ziacare.app.utils.PopupErrorState

sealed class GeneralSettingViewState {

    object Loading : GeneralSettingViewState()
    data class Success(val requestAssistanceLOVResponse: RequestAssistanceLOVResponse? = null) : GeneralSettingViewState()
    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : GeneralSettingViewState()

}