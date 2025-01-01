package kapwad.reader.app.ui.group.viewmodel

import kapwad.reader.app.data.repositories.generalsetting.response.RequestAssistanceLOVResponse
import kapwad.reader.app.utils.PopupErrorState

sealed class GeneralSettingViewState {

    object Loading : GeneralSettingViewState()
    data class Success(val requestAssistanceLOVResponse: RequestAssistanceLOVResponse? = null) : GeneralSettingViewState()
    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : GeneralSettingViewState()

}