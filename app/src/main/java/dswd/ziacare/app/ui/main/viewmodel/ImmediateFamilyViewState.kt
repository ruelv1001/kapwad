package dswd.ziacare.app.ui.main.viewmodel


import dswd.ziacare.app.data.repositories.group.response.ImmediateFamilyResponse
import dswd.ziacare.app.utils.PopupErrorState

sealed class ImmediateFamilyViewState {

    object Loading : ImmediateFamilyViewState()
    data class Success(val immediateFamilyResponse: ImmediateFamilyResponse? = null) : ImmediateFamilyViewState()
    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : ImmediateFamilyViewState()

}