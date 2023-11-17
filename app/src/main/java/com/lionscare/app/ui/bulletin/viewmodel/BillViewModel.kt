package com.lionscare.app.ui.bulletin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lionscare.app.data.model.ErrorModel
import com.lionscare.app.data.repositories.billing.BillRepository
import com.lionscare.app.security.AuthEncryptedDataManager
import com.lionscare.app.utils.AppConstant
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
class BillViewModel @Inject constructor(
    private val billRepository: BillRepository,
    private val encryptedDataManager: AuthEncryptedDataManager
) :
    ViewModel() {

  
    private val _getBillSharedFlow = MutableSharedFlow<BillViewState>()

    val getBillSharedFlow: SharedFlow<BillViewState> =
        _getBillSharedFlow.asSharedFlow()


    private suspend fun loadBills() {
        billRepository.doGetAllBillList()
            .cachedIn(viewModelScope)
            .onStart {
                _getBillSharedFlow.emit(BillViewState.Loading)
            }
            .catch { exception ->
                onError(exception)
                CommonLogger.devLog("error",exception)
            }
            .collect { pagingData ->
                _getBillSharedFlow.emit(
                    BillViewState.SuccessGetAllBillList(pagingData)
                )
            }
    }

    fun refreshBills() {
        viewModelScope.launch {
            loadBills()
        }
    }

    private suspend fun loadAskForDonation() {
        billRepository.doGetAskForDonationList()
            .cachedIn(viewModelScope)
            .onStart {
                _getBillSharedFlow.emit(BillViewState.Loading)
            }
            .catch { exception ->
                onError(exception)
                CommonLogger.devLog("error",exception)
            }
            .collect { pagingData ->
                _getBillSharedFlow.emit(
                    BillViewState.SuccessGetAskForDonationList(pagingData)
                )
            }
    }

    fun refreshDonations() {
        viewModelScope.launch {
            loadAskForDonation()
        }
    }

    private suspend fun onError(exception: Throwable) {
        when (exception) {
            is IOException,
            is TimeoutException,
            -> {
                _getBillSharedFlow.emit(
                    BillViewState.PopupError(
                        PopupErrorState.NetworkError
                    )
                )
            }
            is HttpException -> {
                val errorBody = exception.response()?.errorBody()
                val gson = Gson()
                val type = object : TypeToken<ErrorModel>() {}.type
                var errorResponse: ErrorModel? = gson.fromJson(errorBody?.charStream(), type)
                _getBillSharedFlow.emit(
                    BillViewState.PopupError(
                        if (AppConstant.isSessionStatusCode(errorResponse?.status_code.orEmpty())){
                            PopupErrorState.SessionError
                        }else{
                            PopupErrorState.HttpError
                        }
                        , errorResponse?.msg.orEmpty()
                    )
                )
            }
            else -> _getBillSharedFlow.emit(
                BillViewState.PopupError(
                    PopupErrorState.UnknownError
                )
            )
        }
    }
}