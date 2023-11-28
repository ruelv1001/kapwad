package com.ziacare.app.ui.main.viewmodel

import androidx.paging.PagingData
import com.ziacare.app.data.repositories.group.response.GetGroupListResponse
import com.ziacare.app.data.repositories.group.response.GroupListData
import com.ziacare.app.data.repositories.group.response.PendingGroupRequestData
import com.ziacare.app.ui.group.viewmodel.MemberViewState
import com.ziacare.app.utils.PopupErrorState

sealed class GroupListViewState{
    object Loading : GroupListViewState()
    object LoadingAcceptDeclineInvitation : GroupListViewState()
    data class Success(val pagingData: PagingData<GroupListData>) : GroupListViewState()
    data class SuccessAcceptDeclineInvitation(val msg: String) : GroupListViewState()
    data class SuccessGetPendingRequestList(val pagingData: PagingData<PendingGroupRequestData>) : GroupListViewState()
    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : GroupListViewState()
}

