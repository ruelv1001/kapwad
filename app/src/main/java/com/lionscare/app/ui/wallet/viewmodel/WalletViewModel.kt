package com.lionscare.app.ui.wallet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lionscare.app.data.model.ErrorModel
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
class WalletViewModel @Inject constructor(
    private val walletRepository: WalletRepository
) : ViewModel() {

    private val _walletSharedFlow = MutableSharedFlow<WalletViewState>()

    val walletSharedFlow: SharedFlow<WalletViewState> =
        _walletSharedFlow.asSharedFlow()

    fun getWalletBalance() {
        viewModelScope.launch {
            walletRepository.getWalletBalance()
                .onStart {
                    _walletSharedFlow.emit(WalletViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _walletSharedFlow.emit(
                        WalletViewState.SuccessGetBalance(it.data)
                    )
                }
        }
    }

    fun getTransactionDetails(transactionId: String) {
        viewModelScope.launch {
            walletRepository.getTransactionDetails(transactionId)
                .onStart {
                    _walletSharedFlow.emit(WalletViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _walletSharedFlow.emit(
                        WalletViewState.SuccessTransactionDetail(it.data)
                    )
                }
        }
    }

    fun doTopupPoints(amount: String) {
        viewModelScope.launch {
            walletRepository.doTopupPoints(amount)
                .onStart {
                    _walletSharedFlow.emit(WalletViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _walletSharedFlow.emit(
                        WalletViewState.SuccessTopup(it.web_checkout)
                    )
                }
        }
    }

    fun doScanQr(userId: String) {
        viewModelScope.launch {
            walletRepository.doScanQr(userId)
                .onStart {
                    _walletSharedFlow.emit(WalletViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _walletSharedFlow.emit(
                        WalletViewState.SuccessScanQR(it.data)
                    )
                }
        }
    }

    fun doSendPoints(userId: String, amount: String) {
        viewModelScope.launch {
            walletRepository.doSendPoints(amount, userId)
                .onStart {
                    _walletSharedFlow.emit(WalletViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _walletSharedFlow.emit(
                        WalletViewState.SuccessSendPoint(it.msg)
                    )
                }
        }
    }

    private suspend fun getTransactionList(perPage: Int) {
        val pageConfig = PagingConfig(pageSize = perPage, initialLoadSize = perPage,enablePlaceholders = false)
        walletRepository.getTransactionList(pageConfig)
            .cachedIn(viewModelScope)
            .onStart {
                _walletSharedFlow.emit(WalletViewState.Loading)
            }
            .catch { exception ->
                onError(exception)
                CommonLogger.sysLogE("error",exception.localizedMessage, exception)
            }
            .collect { pagingData ->
                _walletSharedFlow.emit(
                    WalletViewState.SuccessTransactionList(pagingData))
            }
    }

    fun loadTransactionList(perPage: Int = 10) {
        viewModelScope.launch {
            getTransactionList(perPage)
        }
    }

    private suspend fun onError(exception: Throwable) {
        when (exception) {
            is IOException,
            is TimeoutException,
            -> {
                _walletSharedFlow.emit(
                    WalletViewState.PopupError(
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
                    _walletSharedFlow.emit(WalletViewState.InputError(errorResponse.errors))
                } else {
                    _walletSharedFlow.emit(
                        WalletViewState.PopupError(
                            PopupErrorState.HttpError, errorResponse?.msg.orEmpty()
                        )
                    )
                }

            }

            else -> _walletSharedFlow.emit(
                WalletViewState.PopupError(
                    PopupErrorState.UnknownError
                )
            )
        }
    }
}