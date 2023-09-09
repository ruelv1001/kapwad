package com.lionscare.app.ui.group.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lionscare.app.data.model.ErrorModel
import com.lionscare.app.data.repositories.assistance.AssistanceRepository
import com.lionscare.app.data.repositories.assistance.request.CreateAssistanceRequest
import com.lionscare.app.security.AuthEncryptedDataManager
import com.lionscare.app.utils.CommonLogger
import com.lionscare.app.utils.PopupErrorState
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
    encryptedDataManager: AuthEncryptedDataManager
) : ViewModel() {

    val user = encryptedDataManager.getUserBasicInfo()
    private val _assistanceSharedFlow = MutableSharedFlow<AssistanceViewState>()

    val assistanceSharedFlow: SharedFlow<AssistanceViewState> =
        _assistanceSharedFlow.asSharedFlow()

    fun createAssistance(request: CreateAssistanceRequest) {
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
                            PopupErrorState.HttpError, errorResponse?.msg.orEmpty()
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