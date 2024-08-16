package da.farmer.app.ui.main.viewmodel

import androidx.paging.PagingData
import da.farmer.app.data.repositories.group.response.GetGroupListResponse
import da.farmer.app.data.repositories.group.response.GroupListData
import da.farmer.app.data.repositories.group.response.PendingGroupRequestData
import da.farmer.app.ui.group.viewmodel.MemberViewState
import da.farmer.app.utils.PopupErrorState

sealed class GroupListViewState{
    object Loading : GroupListViewState()
    object LoadingAcceptDeclineInvitation : GroupListViewState()
    data class Success(val pagingData: PagingData<GroupListData>) : GroupListViewState()
    data class SuccessAcceptDeclineInvitation(val msg: String) : GroupListViewState()
    data class SuccessGetPendingRequestList(val pagingData: PagingData<PendingGroupRequestData>) : GroupListViewState()
    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : GroupListViewState()
}

