package kapwad.reader.app.ui.group.viewmodel

import androidx.paging.PagingData
import kapwad.reader.app.data.model.ErrorsData
import kapwad.reader.app.data.repositories.assistance.response.CreateAssistanceData
import kapwad.reader.app.data.repositories.assistance.response.CreateAssistanceResponse
import kapwad.reader.app.utils.PopupErrorState

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