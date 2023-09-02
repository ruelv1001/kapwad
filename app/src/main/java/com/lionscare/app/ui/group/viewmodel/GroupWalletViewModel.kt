package com.lionscare.app.ui.group.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lionscare.app.data.model.ErrorModel
import com.lionscare.app.data.repositories.wallet.GroupWalletRepository
import com.lionscare.app.data.repositories.wallet.WalletRepository
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
class GroupWalletViewModel @Inject constructor(
    private val walletRepository: GroupWalletRepository
) : ViewModel() {

    private val _walletSharedFlow = MutableSharedFlow<GroupWalletViewState>()

    val walletSharedFlow: SharedFlow<GroupWalletViewState> =
        _walletSharedFlow.asSharedFlow()

    fun getWalletBalance(groupId: String) {
        viewModelScope.launch {
            walletRepository.getWalletBalance(groupId)
                .onStart {
                    _walletSharedFlow.emit(GroupWalletViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _walletSharedFlow.emit(
                        GroupWalletViewState.SuccessGetBalance(it.data)
                    )
                }
        }
    }

    fun doSendPoints(
        amount: String,
        groupId: String,
        receiverUserId: String = "",
        receiverGroupId: String = ""
    ) {
        viewModelScope.launch {
            walletRepository.doSendPoints(
                amount = amount,
                groupId = groupId,
                receiverUserId = receiverUserId,
                receiverGroupId = receiverGroupId
            )
                .onStart {
                    _walletSharedFlow.emit(GroupWalletViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _walletSharedFlow.emit(
                        GroupWalletViewState.SuccessSendPoint(it.data)
                    )
                }
        }
    }

    private suspend fun getTransactionList(perPage: Int, groupId: String) {
        val pageConfig = PagingConfig(pageSize = perPage, initialLoadSize = perPage,enablePlaceholders = false)
        walletRepository.getTransactionList(pageConfig, groupId = groupId)
            .cachedIn(viewModelScope)
            .onStart {
                _walletSharedFlow.emit(GroupWalletViewState.Loading)
            }
            .catch { exception ->
                onError(exception)
                CommonLogger.sysLogE("error",exception.localizedMessage, exception)
            }
            .collect { pagingData ->
                _walletSharedFlow.emit(
                    GroupWalletViewState.SuccessTransactionList(pagingData))
            }
    }

    fun loadTransactionList(perPage: Int = 10, groupId: String) {
        viewModelScope.launch {
            getTransactionList(perPage, groupId)
        }
    }

    private suspend fun onError(exception: Throwable) {
        when (exception) {
            is IOException,
            is TimeoutException,
            -> {
                _walletSharedFlow.emit(
                    GroupWalletViewState.PopupError(
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
                    _walletSharedFlow.emit(GroupWalletViewState.InputError(errorResponse.errors))
                } else {
                    _walletSharedFlow.emit(
                        GroupWalletViewState.PopupError(
                            PopupErrorState.HttpError, errorResponse?.msg.orEmpty()
                        )
                    )
                }

            }

            else -> _walletSharedFlow.emit(
                GroupWalletViewState.PopupError(
                    PopupErrorState.UnknownError
                )
            )
        }
    }
}