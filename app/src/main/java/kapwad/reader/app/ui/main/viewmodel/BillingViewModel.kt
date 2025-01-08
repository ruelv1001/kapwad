package kapwad.reader.app.data.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kapwad.reader.app.data.model.ErrorModel
import kapwad.reader.app.data.model.ProductOrderListModelData



import kapwad.reader.app.security.AuthEncryptedDataManager

import kapwad.reader.app.utils.AppConstant
import kapwad.reader.app.utils.PopupErrorState
import dagger.hilt.android.lifecycle.HiltViewModel
import kapwad.reader.app.data.model.CreatedBillListModelData
import kapwad.reader.app.data.repositories.bill.BillingRepository
import kapwad.reader.app.data.repositories.bill.BillingService
import kapwad.reader.app.data.repositories.crops.request.CropDetailsRequest
import kapwad.reader.app.ui.crops.viemodel.CropsViewState
import kapwad.reader.app.ui.main.viewmodel.BillingViewState
import kapwad.reader.app.ui.main.viewmodel.ConsumerViewState
import kapwad.reader.app.ui.main.viewmodel.MeterViewState
import kapwad.reader.app.utils.CommonLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeoutException
import javax.inject.Inject

@HiltViewModel
class BillingViewModel @Inject constructor(
    private val billingRepository: BillingRepository,
    authEncryptedDataManager: AuthEncryptedDataManager,
    private val encryptedDataManager: AuthEncryptedDataManager
) : ViewModel() {
    val user = encryptedDataManager.getUserBasicInfo()
    private val _billingStateFlow = MutableStateFlow<BillingViewState>(BillingViewState.Idle)
    val billingStateFlow: StateFlow<BillingViewState> = _billingStateFlow.asStateFlow()


    // Add or update a cart item

    fun getUploadJson(upload: String) {
        viewModelScope.launch {
            billingRepository.uploadJson(upload)
                .onStart {
                    _billingStateFlow.emit(BillingViewState.Loading)
                }
                .catch { exception ->
                    CommonLogger.instance.sysLogE("BillingViewModel Error ", exception.localizedMessage, exception)
                    val errorMessage = exception.localizedMessage ?: exception.toString()
                    _billingStateFlow.emit(BillingViewState.Error(errorMessage))
                }
                .collect { response ->
                    _billingStateFlow.emit(BillingViewState.SuccessUpload(response))
                }
        }
    }

    fun insertBilling(order: CreatedBillListModelData) {
        viewModelScope.launch {
            billingRepository.createBilling(order)
                .onStart {
                    _billingStateFlow.emit(BillingViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _billingStateFlow.emit(BillingViewState.SuccessOfflineCreateOrder(it))
                }
        }
    }

    fun getBilling() {
        viewModelScope.launch {
            billingRepository.getBilling()
                .onStart {
                    _billingStateFlow.emit(BillingViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _billingStateFlow.emit(BillingViewState.SuccessOfflineGetOrder(it))
                }
        }
    }




    fun deleteAllOrder() {
        viewModelScope.launch {
            billingRepository.deleteAllBilling()
                .onStart {
                    _billingStateFlow.emit(BillingViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _billingStateFlow.emit(BillingViewState.SuccessDelete(it.toString()))
                }
        }
    }



    fun getValidatedOnData(month: String, owners_id: String) {
        viewModelScope.launch {
            billingRepository.getMeterByCredential(month, owners_id)
                .onStart {
                    _billingStateFlow.emit(BillingViewState.Loading)
                }
                .catch { exception ->
                    onError(exception) // Handle exceptions
                }
                .collect { response ->
                    if (response != null) {
                        Log.d("GetConsumerById", "Response: $response")
                        _billingStateFlow.emit(BillingViewState.SuccessExisted(response))


                    } else {
                        Log.d("GetConsumerById", "Response is null")
                        _billingStateFlow.emit(BillingViewState.Error("No Data"))
                    }
                }
        }
    }


    fun searchConsumer(searchQuery: String) {
        viewModelScope.launch {
            billingRepository.searchConsumer(searchQuery)
                .onStart {
                    _billingStateFlow.emit(BillingViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect { response ->
                    Log.d("SearchConsumer", "Response: $response")
                    _billingStateFlow.emit(BillingViewState.SuccessOfflineGetSearch   (response))
                }
        }
    }

    private suspend fun onError(exception: Throwable) {
        when (exception) {
            is IOException,
            is TimeoutException,
            -> {
                _billingStateFlow.emit(
                    BillingViewState.PopupError(
                        PopupErrorState.NetworkError
                    )
                )
            }

            is HttpException -> {
                val errorBody = exception.response()?.errorBody()
                val gson = Gson()
                val type = object : TypeToken<ErrorModel>() {}.type
                var errorResponse: ErrorModel? = gson.fromJson(errorBody?.charStream(), type)
                _billingStateFlow.emit(
                    BillingViewState.PopupError(
                        if (AppConstant.isSessionStatusCode(errorResponse?.status_code.orEmpty())) {
                            PopupErrorState.SessionError
                        } else {
                            PopupErrorState.HttpError
                        }, errorResponse?.msg.orEmpty()
                    )
                )
            }

            else -> _billingStateFlow.emit(
                BillingViewState.PopupError(
                    PopupErrorState.UnknownError
                )
            )
        }
    }
}
