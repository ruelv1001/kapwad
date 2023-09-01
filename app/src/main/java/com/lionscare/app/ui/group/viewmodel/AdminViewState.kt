package com.lionscare.app.ui.group.viewmodel

import androidx.paging.PagingData
import com.lionscare.app.data.model.ErrorsData
import com.lionscare.app.data.repositories.member.response.MemberListData
import com.lionscare.app.data.repositories.member.response.PendingMemberData
import com.lionscare.app.utils.PopupErrorState

sealed class AdminViewState {

    object Loading : AdminViewState()

    data class SuccessGetListOfAdmin(val pagingData: PagingData<MemberListData>) : AdminViewState()
    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : AdminViewState()
    data class InputError(val errorData: ErrorsData? = null) : AdminViewState()

}