package kapwad.reader.app.ui.main.viewmodel


import kapwad.reader.app.data.repositories.group.response.ImmediateFamilyResponse
import kapwad.reader.app.utils.PopupErrorState

sealed class ImmediateFamilyViewState {

    object Loading : ImmediateFamilyViewState()
    data class Success(val immediateFamilyResponse: ImmediateFamilyResponse? = null) : ImmediateFamilyViewState()
    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : ImmediateFamilyViewState()

}