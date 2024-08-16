package da.farmer.app.ui.group.viewmodel

import androidx.paging.PagingData
import da.farmer.app.data.model.ErrorsData
import da.farmer.app.data.repositories.member.response.MemberListData
import da.farmer.app.utils.PopupErrorState

sealed class AdminViewState {

    object Loading : AdminViewState()
    data class SuccessGetListOfAdmin(val pagingData: PagingData<MemberListData>) : AdminViewState()
    data class SuccessRemoveMember(val message: String = ""): AdminViewState()
    data class SuccessPromoteMember(val message: String = ""): AdminViewState()
    data class SuccessDemoteAdmin(val message: String = ""): AdminViewState()
    data class SuccessTransferOwnership(val message: String = ""): AdminViewState()
    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : AdminViewState()
    data class InputError(val errorData: ErrorsData? = null) : AdminViewState()

}