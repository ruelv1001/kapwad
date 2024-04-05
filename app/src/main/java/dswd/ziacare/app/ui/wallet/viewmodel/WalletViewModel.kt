package dswd.ziacare.app.ui.wallet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dswd.ziacare.app.data.model.ErrorModel
import dswd.ziacare.app.data.repositories.group.response.GroupData
import dswd.ziacare.app.data.repositories.wallet.WalletRepository
import dswd.ziacare.app.security.AuthEncryptedDataManager
import dswd.ziacare.app.utils.AppConstant
import dswd.ziacare.app.utils.CommonLogger
import dswd.ziacare.app.utils.PopupErrorState
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
    private val walletRepository: WalletRepository,
    private val encryptedDataManager: AuthEncryptedDataManager
) : ViewModel() {

    private val _walletSharedFlow = MutableSharedFlow<WalletViewState>()
    val user = encryptedDataManager.getUserBasicInfo()

    val walletSharedFlow: SharedFlow<WalletViewState> =
        _walletSharedFlow.asSharedFlow()

    fun getKycStatus() :String{
        return encryptedDataManager.getKYCStatus()
    }

    fun getWalletBalance() {
        viewModelScope.launch {
            walletRepository.getWalletBalance()
                .onStart {
                    _walletSharedFlow.emit(WalletViewState.Loading)
                }
                .catch { exception ->
                    CommonLogger.sysLogE("wallet", exception.localizedMessage, exception)
                    onError(exception)
                }
                .collect {
                    _walletSharedFlow.emit(
                        WalletViewState.SuccessGetBalance(it.data)
                    )
                }
        }
    }

    fun doSearchUser(keyword: String) {
        viewModelScope.launch {
            walletRepository.doSearchUser(keyword)
                .onStart {
                    _walletSharedFlow.emit(WalletViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _walletSharedFlow.emit(
                        WalletViewState.SuccessSearchUser(it.data.orEmpty())
                    )
                }
        }
    }

    fun doSearchGroup(keyword: String) {
        viewModelScope.launch {
            walletRepository.doSearchGroup(keyword)
                .onStart {

                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _walletSharedFlow.emit(
                        WalletViewState.SuccessSearchGroup(it.data.orEmpty())
                    )
                }
        }
    }

    fun doScanGroup(keyword: String) {
        viewModelScope.launch {
            walletRepository.doSearchGroup(keyword)
                .onStart {
                    _walletSharedFlow.emit(WalletViewState.LoadingScanGroup)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _walletSharedFlow.emit(
                        WalletViewState.SuccessScanGroup(it.data?.get(0)?: GroupData())
                    )
                }
        }
    }

    fun doSearchGroupWithLoading(keyword: String) {
        viewModelScope.launch {
            walletRepository.doSearchGroup(keyword)
                .onStart {
                    _walletSharedFlow.emit(WalletViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _walletSharedFlow.emit(
                        WalletViewState.SuccessSearchGroup(it.data.orEmpty())
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

    fun doSendPoints(userId: String = "", groupId: String = "", amount: String, notes: String = "") {
        viewModelScope.launch {
            walletRepository.doSendPoints(
                userId = userId,
                groupId = groupId,
                amount = amount,
                notes = notes
            )
                .onStart {
                    _walletSharedFlow.emit(WalletViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _walletSharedFlow.emit(
                        WalletViewState.SuccessSendPoint(it.data)
                    )
                }
        }
    }

    fun doScan2Pay(amount: String, mid: String, remarks: String = "") {
        viewModelScope.launch {
            walletRepository.doScan2Pay(amount, mid, remarks)
                .onStart {
                    _walletSharedFlow.emit(WalletViewState.LoadingScan)
                }
                .catch { exception ->
                    onError(exception)

                }
                .collect {
                    _walletSharedFlow.emit(
                        WalletViewState.SuccessScan2Pay(it.msg.orEmpty())
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

            else -> _walletSharedFlow.emit(
                WalletViewState.PopupError(
                    PopupErrorState.UnknownError
                )
            )
        }
    }
}