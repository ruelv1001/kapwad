package da.farmer.app.ui.group.viewmodel

import da.farmer.app.data.repositories.generalsetting.response.RequestAssistanceLOVResponse
import da.farmer.app.utils.PopupErrorState

sealed class GeneralSettingViewState {

    object Loading : GeneralSettingViewState()
    data class Success(val requestAssistanceLOVResponse: RequestAssistanceLOVResponse? = null) : GeneralSettingViewState()
    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : GeneralSettingViewState()

}