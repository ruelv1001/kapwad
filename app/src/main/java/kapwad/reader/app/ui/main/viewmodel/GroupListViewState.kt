package kapwad.reader.app.ui.main.viewmodel

import androidx.paging.PagingData
import kapwad.reader.app.data.repositories.group.response.GroupListData
import kapwad.reader.app.data.repositories.group.response.PendingGroupRequestData
import kapwad.reader.app.utils.PopupErrorState

sealed class GroupListViewState{
    object Loading : GroupListViewState()
    object LoadingAcceptDeclineInvitation : GroupListViewState()
    data class Success(val pagingData: PagingData<GroupListData>) : GroupListViewState()
    data class SuccessAcceptDeclineInvitation(val msg: String) : GroupListViewState()
    data class SuccessGetPendingRequestList(val pagingData: PagingData<PendingGroupRequestData>) : GroupListViewState()
    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : GroupListViewState()
}

