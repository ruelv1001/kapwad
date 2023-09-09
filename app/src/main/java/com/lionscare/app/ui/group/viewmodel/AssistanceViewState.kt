package com.lionscare.app.ui.group.viewmodel

import androidx.paging.PagingData
import com.lionscare.app.data.model.ErrorsData
import com.lionscare.app.data.repositories.member.response.MemberListData
import com.lionscare.app.utils.PopupErrorState

sealed class AssistanceViewState {

    object Loading : AssistanceViewState()
    data class SuccessGetListOfAssistance(val pagingData: PagingData<MemberListData>) : AssistanceViewState()
    data class SuccessCreateAssistance(val message: String = ""): AssistanceViewState()
    data class SuccessCancelAssistance(val message: String = ""): AssistanceViewState()
    data class SuccessApproveDeclineAssistance(val message: String = ""): AssistanceViewState()
    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : AssistanceViewState()
    data class InputError(val errorData: ErrorsData? = null) : AssistanceViewState()

}