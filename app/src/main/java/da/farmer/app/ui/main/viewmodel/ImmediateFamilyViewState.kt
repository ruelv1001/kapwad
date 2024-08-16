package da.farmer.app.ui.main.viewmodel


import da.farmer.app.data.repositories.group.response.ImmediateFamilyResponse
import da.farmer.app.utils.PopupErrorState

sealed class ImmediateFamilyViewState {

    object Loading : ImmediateFamilyViewState()
    data class Success(val immediateFamilyResponse: ImmediateFamilyResponse? = null) : ImmediateFamilyViewState()
    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : ImmediateFamilyViewState()

}