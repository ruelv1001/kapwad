package com.ziacare.app.ui.group.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ziacare.app.data.model.ErrorModel
import com.ziacare.app.data.repositories.assistance.AssistanceRepository
import com.ziacare.app.data.repositories.assistance.request.CreateAssistanceRequest
import com.ziacare.app.security.AuthEncryptedDataManager
import com.ziacare.app.utils.AppConstant
import com.ziacare.app.utils.CommonLogger
import com.ziacare.app.utils.PopupErrorState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeoutException
import javax.inject.Inject

@HiltViewModel
class AssistanceViewModel @Inject constructor(
    private val assistanceRepository: AssistanceRepository,
    private val encryptedDataManager: AuthEncryptedDataManager
) : ViewModel() {

    val user = encryptedDataManager.getUserBasicInfo()
    private val _assistanceSharedFlow = MutableSharedFlow<AssistanceViewState>()

    val assistanceSharedFlow: SharedFlow<AssistanceViewState> =
        _assistanceSharedFlow.asSharedFlow()

    fun getUserKYC() : String{
        return encryptedDataManager.getKYCStatus()
    }

    fun createAssistance(request: CreateAssistanceRequest){
        viewModelScope.launch {
            assistanceRepository.doCreateAssistance(request)
                .onStart {
                    _assistanceSharedFlow.emit(AssistanceViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _assistanceSharedFlow.emit(
                        AssistanceViewState.SuccessCreateAssistance(it.msg.orEmpty())
                    )
                }
        }
    }

    fun cancelAssistance(referenceId: String, groupId: String) {
        viewModelScope.launch {
            assistanceRepository.doCancelAssistanceRequest(referenceId, groupId)
                .onStart {
                    _assistanceSharedFlow.emit(AssistanceViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _assistanceSharedFlow.emit(
                        AssistanceViewState.SuccessCancelAssistance(it.msg.orEmpty())
                    )
                }
        }
    }

    fun approveAssistance(referenceId: String, groupId: String, remarks: String? = null) {
        viewModelScope.launch {
            assistanceRepository.doApproveAssistanceRequest(referenceId, groupId, remarks)
                .onStart {
                    _assistanceSharedFlow.emit(AssistanceViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _assistanceSharedFlow.emit(
                        AssistanceViewState.SuccessApproveDeclineAssistance(it.msg.orEmpty())
                    )
                }
        }
    }

    fun declineAssistance(referenceId: String, groupId: String, remarks: String? = null) {
        viewModelScope.launch {
            assistanceRepository.doDeclineAssistanceRequest(referenceId, groupId, remarks)
                .onStart {
                    _assistanceSharedFlow.emit(AssistanceViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _assistanceSharedFlow.emit(
                        AssistanceViewState.SuccessApproveDeclineAssistance(it.msg.orEmpty())
                    )
                }
        }
    }

    fun getAssistanceInfo(referenceId: String, groupId: String) {
        viewModelScope.launch {
            assistanceRepository.doGetAssistanceRequestInfo(referenceId, groupId)
                .onStart {
                    _assistanceSharedFlow.emit(AssistanceViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _assistanceSharedFlow.emit(
                        AssistanceViewState.SuccessGetAssistanceInfo(it)
                    )
                }
        }
    }

    private suspend fun loadAllAssistanceRequest(
        groupId: String,
        filter: List<String>,
        isLimited: Boolean? = false
    ) {
        assistanceRepository.doGetAllListOfAssistanceRequest(
            groupId = groupId,
            filter = filter,
            isLimited = isLimited
        )
            .cachedIn(viewModelScope)
            .onStart {
                _assistanceSharedFlow.emit(AssistanceViewState.Loading)
            }
            .catch { exception ->
                onError(exception)
            }
            .collect { pagingData ->
                _assistanceSharedFlow.emit(
                    AssistanceViewState.SuccessGetAllListOfAssistance(pagingData)
                )
            }
    }

    fun refresh(groupId: String, filter: List<String>, isLimited: Boolean? = false) {
        viewModelScope.launch {
            loadAllAssistanceRequest(groupId, filter, isLimited)
        }
    }

    private suspend fun loadMyAssistanceRequest(groupId: String, filter: List<String>) {
        assistanceRepository.doGetMyListOfAssistanceRequest(groupId = groupId, filter = filter)
            .cachedIn(viewModelScope)
            .onStart {
                _assistanceSharedFlow.emit(AssistanceViewState.Loading)
            }
            .catch { exception ->
                onError(exception)
            }
            .collect { pagingData ->
                _assistanceSharedFlow.emit(
                    AssistanceViewState.SuccessGetMyListOfAssistance(pagingData)
                )
            }
    }

    fun refreshMyList(groupId: String, filter: List<String>) {
        viewModelScope.launch {
            loadMyAssistanceRequest(groupId, filter)
        }
    }

    private suspend fun onError(exception: Throwable) {
        when (exception) {
            is IOException,
            is TimeoutException,
            -> {
                _assistanceSharedFlow.emit(
                    AssistanceViewState.PopupError(
                        PopupErrorState.NetworkError
                    )
                )
            }

            is HttpException -> {
                val errorBody = exception.response()?.errorBody()
                val gson = Gson()
                val type = object : TypeToken<ErrorModel>() {}.type
                var errorResponse: ErrorModel? = gson.fromJson(errorBody?.charStream(), type)
                if (errorResponse?.has_requirements == true) {
                    _assistanceSharedFlow.emit(AssistanceViewState.InputError(errorResponse.errors))
                } else {
                    _assistanceSharedFlow.emit(
                        AssistanceViewState.PopupError(
                            if (AppConstant.isSessionStatusCode(errorResponse?.status_code.orEmpty())){
                                PopupErrorState.SessionError
                            }else{
                                PopupErrorState.HttpError
                            }
                            , errorResponse?.msg.orEmpty()
                        )
                    )
                }

            }

            else -> _assistanceSharedFlow.emit(
                AssistanceViewState.PopupError(
                    PopupErrorState.UnknownError
                )
            )
        }
    }
}