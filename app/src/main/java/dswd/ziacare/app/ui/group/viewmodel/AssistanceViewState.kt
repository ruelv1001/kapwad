package dswd.ziacare.app.ui.group.viewmodel

import androidx.paging.PagingData
import dswd.ziacare.app.data.model.ErrorsData
import dswd.ziacare.app.data.repositories.assistance.response.CreateAssistanceData
import dswd.ziacare.app.data.repositories.assistance.response.CreateAssistanceResponse
import dswd.ziacare.app.data.repositories.member.response.MemberListData
import dswd.ziacare.app.utils.PopupErrorState

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