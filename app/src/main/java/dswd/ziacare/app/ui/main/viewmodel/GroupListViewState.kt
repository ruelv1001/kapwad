package dswd.ziacare.app.ui.main.viewmodel

import androidx.paging.PagingData
import dswd.ziacare.app.data.repositories.group.response.GetGroupListResponse
import dswd.ziacare.app.data.repositories.group.response.GroupListData
import dswd.ziacare.app.data.repositories.group.response.PendingGroupRequestData
import dswd.ziacare.app.ui.group.viewmodel.MemberViewState
import dswd.ziacare.app.utils.PopupErrorState

sealed class GroupListViewState{
    object Loading : GroupListViewState()
    object LoadingAcceptDeclineInvitation : GroupListViewState()
    data class Success(val pagingData: PagingData<GroupListData>) : GroupListViewState()
    data class SuccessAcceptDeclineInvitation(val msg: String) : GroupListViewState()
    data class SuccessGetPendingRequestList(val pagingData: PagingData<PendingGroupRequestData>) : GroupListViewState()
    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : GroupListViewState()
}

