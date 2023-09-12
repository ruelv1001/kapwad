package com.lionscare.app.ui.group.viewmodel

import androidx.paging.PagingData
import com.lionscare.app.data.model.ErrorsData
import com.lionscare.app.data.repositories.assistance.response.CreateAssistanceData
import com.lionscare.app.data.repositories.assistance.response.CreateAssistanceResponse
import com.lionscare.app.data.repositories.member.response.MemberListData
import com.lionscare.app.utils.PopupErrorState

sealed class AssistanceViewState {

    object Loading : AssistanceViewState()
    data class SuccessGetAllListOfAssistance(val pagingData: PagingData<CreateAssistanceData>) : AssistanceViewState()
    data class SuccessGetMyListOfAssistance(val pagingData: PagingData<CreateAssistanceData>) : AssistanceViewState()
    data class SuccessCreateAssistance(val message: String = ""): AssistanceViewState()
    data class SuccessCancelAssistance(val message: String = ""): AssistanceViewState()
    data class SuccessApproveDeclineAssistance(val message: String = ""): AssistanceViewState()
    data class SuccessGetAssistanceInfo(val response : CreateAssistanceResponse?= null): AssistanceViewState()
    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : AssistanceViewState()
    data class InputError(val errorData: ErrorsData? = null) : AssistanceViewState()

}