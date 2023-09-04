package com.lionscare.app.ui.main.viewmodel

import androidx.paging.PagingData
import com.lionscare.app.data.repositories.group.response.GroupListData
import com.lionscare.app.data.repositories.group.response.PendingGroupRequestData
import com.lionscare.app.utils.PopupErrorState

sealed class GroupListViewState{
    object Loading : GroupListViewState()
    data class Success(val pagingData: PagingData<GroupListData>) : GroupListViewState()
    data class SuccessGetPendingRequestList(val pagingData: PagingData<PendingGroupRequestData>) : GroupListViewState()
    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : GroupListViewState()
}

