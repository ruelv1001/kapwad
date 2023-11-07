package com.lionscare.app.ui.billing.viewmodel

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lionscare.app.data.model.ErrorModel
import com.lionscare.app.security.AuthEncryptedDataManager
import com.lionscare.app.ui.billing.viewstate.BillingViewState
import com.lionscare.app.utils.AppConstant
import com.lionscare.app.utils.PopupErrorState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeoutException
import javax.inject.Inject

@HiltViewModel
class BillingViewModel  @Inject constructor(
    private val encryptedDataManager: AuthEncryptedDataManager
)   : ViewModel() {
    // Shared Flows
    private val _billingSharedFlow = MutableSharedFlow<BillingViewState>()
    val billingSharedFlow: SharedFlow<BillingViewState> =
        _billingSharedFlow.asSharedFlow()

    private suspend fun onError(exception: Throwable) {
        when (exception) {
            is IOException,
            is TimeoutException,
            -> {
                _billingSharedFlow.emit(
                    BillingViewState.PopupError(
                        PopupErrorState.NetworkError
                    )
                )
            }
            is HttpException -> {
                val errorBody = exception.response()?.errorBody()
                val gson = Gson()
                val type = object : TypeToken<ErrorModel>() {}.type
                val errorResponse: ErrorModel? = gson.fromJson(errorBody?.charStream(), type)

                if (errorResponse?.has_requirements == true) {
                    _billingSharedFlow.emit(BillingViewState.InputError(errorResponse.errors))
                } else {
                    if(AppConstant.isSessionStatusCode(errorResponse?.status_code.orEmpty())){
                        _billingSharedFlow.emit(
                            BillingViewState.PopupError(
                                PopupErrorState.SessionError,
                                errorResponse?.msg.orEmpty()
                            )
                        )
                    }else if (errorResponse?.status_code.orEmpty() != AppConstant.NOT_FOUND){
                        _billingSharedFlow.emit(
                            BillingViewState.PopupError(
                                PopupErrorState.HttpError,
                                errorResponse?.msg.orEmpty()
                            )
                        )
                    }
                }
            }
            else -> _billingSharedFlow.emit(
                BillingViewState.PopupError(
                    PopupErrorState.UnknownError
                )
            )
        }
    }
}