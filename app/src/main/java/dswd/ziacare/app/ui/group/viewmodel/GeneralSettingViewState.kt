package dswd.ziacare.app.ui.group.viewmodel

import dswd.ziacare.app.data.repositories.generalsetting.response.RequestAssistanceLOVResponse
import dswd.ziacare.app.utils.PopupErrorState

sealed class GeneralSettingViewState {

    object Loading : GeneralSettingViewState()
    data class Success(val requestAssistanceLOVResponse: RequestAssistanceLOVResponse? = null) : GeneralSettingViewState()
    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : GeneralSettingViewState()

}