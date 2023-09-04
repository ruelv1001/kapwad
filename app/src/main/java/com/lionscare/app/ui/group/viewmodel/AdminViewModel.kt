package com.lionscare.app.ui.group.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lionscare.app.data.model.ErrorModel
import com.lionscare.app.data.repositories.admin.AdminRepository
import com.lionscare.app.ui.onboarding.viewmodel.LoginViewState
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
class AdminViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _adminSharedFlow = MutableSharedFlow<AdminViewState>()

    val adminSharedFlow: SharedFlow<AdminViewState> =
        _adminSharedFlow.asSharedFlow()

    private suspend fun loadAdminList(groupId: String) {
        adminRepository.doGetListOfAdmin(groupId = groupId)
            .cachedIn(viewModelScope)
            .onStart {
                _adminSharedFlow.emit(AdminViewState.Loading)
            }
            .catch { exception ->
                onError(exception)
                CommonLogger.devLog("error", exception)
            }
            .collect { pagingData ->
                _adminSharedFlow.emit(
                    AdminViewState.SuccessGetListOfAdmin(pagingData)
                )
            }
    }

    fun refresh(groupId: String) {
        viewModelScope.launch {
            loadAdminList(groupId)
        }
    }

    fun doRemoveMember(groupId: String, memberId: Int) {
        viewModelScope.launch {
            adminRepository.doRemoveMember(groupId,memberId)
                .onStart {
                    _adminSharedFlow.emit(AdminViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _adminSharedFlow.emit(
                        AdminViewState.SuccessRemoveMember(it.msg.orEmpty())
                    )
                }
        }
    }
    fun doDemoteAdmin(groupId: String, memberId: Int) {
        viewModelScope.launch {
            adminRepository.doDemoteAdmin(groupId,memberId)
                .onStart {
                    _adminSharedFlow.emit(AdminViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _adminSharedFlow.emit(
                        AdminViewState.SuccessDemoteAdmin(it.msg.orEmpty())
                    )
                }
        }
    }
    fun doPromoteMember(groupId: String, memberId: Int) {
        viewModelScope.launch {
            adminRepository.doPromoteMember(groupId,memberId)
                .onStart {
                    _adminSharedFlow.emit(AdminViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _adminSharedFlow.emit(
                        AdminViewState.SuccessPromoteMember(it.msg.orEmpty())
                    )
                }
        }
    }
    

    private suspend fun onError(exception: Throwable) {
        when (exception) {
            is IOException,
            is TimeoutException,
            -> {
                _adminSharedFlow.emit(
                    AdminViewState.PopupError(
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
                    _adminSharedFlow.emit(AdminViewState.InputError(errorResponse.errors))
                } else {
                    _adminSharedFlow.emit(
                        AdminViewState.PopupError(
                            PopupErrorState.HttpError, errorResponse?.msg.orEmpty()
                        )
                    )
                }

            }

            else -> _adminSharedFlow.emit(
                AdminViewState.PopupError(
                    PopupErrorState.UnknownError
                )
            )
        }
    }
}