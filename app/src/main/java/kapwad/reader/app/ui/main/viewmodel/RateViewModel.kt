package kapwad.reader.app.data.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kapwad.reader.app.data.model.ErrorModel


import kapwad.reader.app.security.AuthEncryptedDataManager

import kapwad.reader.app.utils.AppConstant
import kapwad.reader.app.utils.PopupErrorState
import dagger.hilt.android.lifecycle.HiltViewModel
import kapwad.reader.app.data.model.ConsumerListModelData
import kapwad.reader.app.data.model.RateAListModelData
import kapwad.reader.app.data.model.RateBListModelData
import kapwad.reader.app.data.model.RateCListModelData
import kapwad.reader.app.data.model.RateListModelData
import kapwad.reader.app.data.model.RateReListModelData
import kapwad.reader.app.data.model.TempListModelData


import kapwad.reader.app.data.repositories.waterrate.RateRepository
import kapwad.reader.app.ui.main.viewmodel.ConsumerViewState
import kapwad.reader.app.ui.main.viewmodel.RateViewState
import kapwad.reader.app.ui.main.viewmodel.TempViewState


import kapwad.reader.app.utils.CommonLogger
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeoutException
import javax.inject.Inject

@HiltViewModel
class RateViewModel @Inject constructor(
    private val rateRepository: RateRepository,
    authEncryptedDataManager: AuthEncryptedDataManager
) : ViewModel() {

    private val _rateStateFlow = MutableStateFlow<RateViewState>(RateViewState.Idle)
    val rateStateFlow: StateFlow<RateViewState> = _rateStateFlow.asStateFlow()

    //get by id

    fun getRateReById(id: String) {
        viewModelScope.launch {
            rateRepository.getRateReById(id)
                .onStart {
                    _rateStateFlow.emit(RateViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect { response ->
                    Log.d("GetConsumerById", "Response: $response")
                    _rateStateFlow.emit(RateViewState.SuccessRateReById(response))
                }
        }
    }
    fun getRateCById(id: String) {
        viewModelScope.launch {
            rateRepository.getRateCById(id)
                .onStart {
                    _rateStateFlow.emit(RateViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect { response ->
                    Log.d("GetConsumerById", "Response: $response")
                    _rateStateFlow.emit(RateViewState.SuccessRateCById(response))
                }
        }
    }

    fun getRateBById(id: String) {
        viewModelScope.launch {
            rateRepository.getRateBById(id)
                .onStart {
                    _rateStateFlow.emit(RateViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect { response ->
                    Log.d("GetConsumerById", "Response: $response")
                    _rateStateFlow.emit(RateViewState.SuccessRateBById(response))
                }
        }
    }

    fun getRateAById(id: String) {
        viewModelScope.launch {
            rateRepository.getRateAById(id)
                .onStart {
                    _rateStateFlow.emit(RateViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect { response ->
                    Log.d("GetConsumerById", "Response: $response")
                    _rateStateFlow.emit(RateViewState.SuccessRateAById(response))
                }
        }
    }

    fun getRateById(id: String) {
        viewModelScope.launch {
            rateRepository.getRateById(id)
                .onStart {
                    _rateStateFlow.emit(RateViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect { response ->
                    Log.d("GetConsumerById", "Response: $response")
                    _rateStateFlow.emit(RateViewState.SuccessRateById(response))
                }
        }
    }


    //-----------------------------




    fun insertWR(data: List<RateListModelData>) {
        viewModelScope.launch {
            rateRepository.create(data)
                .onStart { _rateStateFlow.emit(RateViewState.Loading) }
                .catch { exception -> onError(exception) }
                .collect { _rateStateFlow.emit(RateViewState.SuccessOfflineCreate(it)) }
        }
    }

    fun insertWRA(data: List<RateAListModelData>) {
        viewModelScope.launch {
            rateRepository.createA(data)
                .onStart { _rateStateFlow.emit(RateViewState.Loading) }
                .catch { exception -> onError(exception) }
                .collect { _rateStateFlow.emit(RateViewState.SuccessOfflineCreateA(it)) }
        }
    }

    fun insertWRB(data: List<RateBListModelData>) {
        viewModelScope.launch {
            rateRepository.createB(data)
                .onStart { _rateStateFlow.emit(RateViewState.Loading) }
                .catch { exception -> onError(exception) }
                .collect { _rateStateFlow.emit(RateViewState.SuccessOfflineCreateB(it)) }
        }
    }

    fun insertWRC(data: List<RateCListModelData>) {
        viewModelScope.launch {
            rateRepository.createC(data)
                .onStart { _rateStateFlow.emit(RateViewState.Loading) }
                .catch { exception -> onError(exception) }
                .collect { _rateStateFlow.emit(RateViewState.SuccessOfflineCreateC(it)) }
        }
    }

    fun insertWRRe(data: List<RateReListModelData>) {
        viewModelScope.launch {
            rateRepository.createRe(data)
                .onStart { _rateStateFlow.emit(RateViewState.Loading) }
                .catch { exception -> onError(exception) }
                .collect { _rateStateFlow.emit(RateViewState.SuccessOfflineCreateRe(it)) }
        }
    }


//------------------------------------------


    fun getRate() {
        viewModelScope.launch {
            rateRepository.getRate()
                .onStart {
                    _rateStateFlow.emit(RateViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _rateStateFlow.emit(RateViewState.SuccessOfflineGetOrder(it))
                }
        }
    }

    fun getRateA() {
        viewModelScope.launch {
            rateRepository.getRateA()
                .onStart {
                    _rateStateFlow.emit(RateViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _rateStateFlow.emit(RateViewState.SuccessOfflineGetOrderA(it))
                }
        }
    }

    fun getRateB() {
        viewModelScope.launch {
            rateRepository.getRateB()
                .onStart {
                    _rateStateFlow.emit(RateViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _rateStateFlow.emit(RateViewState.SuccessOfflineGetOrderB(it))
                }
        }
    }

    fun getRateC() {
        viewModelScope.launch {
            rateRepository.getRateC()
                .onStart {
                    _rateStateFlow.emit(RateViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _rateStateFlow.emit(RateViewState.SuccessOfflineGetOrderC(it))
                }
        }
    }



//---------------------------


    fun deleteAllTWR() {
        viewModelScope.launch {
            rateRepository.deleteAllTWR()
                .onStart {
                    _rateStateFlow.emit(RateViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _rateStateFlow.emit(RateViewState.SuccessDelete(it.toString()))
                }
        }
    }

    fun deleteAllTWRA() {
        viewModelScope.launch {
            rateRepository.deleteAllTWRA()
                .onStart {
                    _rateStateFlow.emit(RateViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _rateStateFlow.emit(RateViewState.SuccessDelete(it.toString()))
                }
        }
    }

    fun deleteAllTWRB() {
        viewModelScope.launch {
            rateRepository.deleteAllTWRB()
                .onStart {
                    _rateStateFlow.emit(RateViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _rateStateFlow.emit(RateViewState.SuccessDelete(it.toString()))
                }
        }
    }


    fun deleteAllTWRC() {
        viewModelScope.launch {
            rateRepository.deleteAllTWRC()
                .onStart {
                    _rateStateFlow.emit(RateViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _rateStateFlow.emit(RateViewState.SuccessDelete(it.toString()))
                }
        }
    }



//---------------------------------
    fun getWROnlineList() {
        viewModelScope.launch {
            rateRepository.getAllWR()
                .onStart {
                    _rateStateFlow.emit(RateViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                    CommonLogger.sysLogE("LOGzERROR", exception.localizedMessage, exception)
                }
                .onEach { consumerList ->
                    // Convert the list to JSON
                    val gson = Gson()
                    val jsonData = gson.toJson(consumerList)

                    // Emit success with JSON data and the object list
                    _rateStateFlow.emit(
                        RateViewState.SuccessOnlineRate(jsonData, consumerList)
                    )
                }
                .flowOn(Dispatchers.IO)
                .launchIn(viewModelScope)
        }
    }


    fun getWRAOnlineList() {
        viewModelScope.launch {
            rateRepository.getAllWRA()
                .onStart {
                    _rateStateFlow.emit(RateViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                    CommonLogger.sysLogE("LOGzERROR", exception.localizedMessage, exception)
                }
                .onEach { consumerList ->
                    // Convert the list to JSON
                    val gson = Gson()
                    val jsonData = gson.toJson(consumerList)

                    // Emit success with JSON data and the object list
                    _rateStateFlow.emit(
                        RateViewState.SuccessOnlineRateA(jsonData, consumerList)
                    )
                }
                .flowOn(Dispatchers.IO)
                .launchIn(viewModelScope)
        }
    }

    fun getWRBOnlineList() {
        viewModelScope.launch {
            rateRepository.getAllWRB()
                .onStart {
                    _rateStateFlow.emit(RateViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                    CommonLogger.sysLogE("LOGzERROR", exception.localizedMessage, exception)
                }
                .onEach { consumerList ->
                    // Convert the list to JSON
                    val gson = Gson()
                    val jsonData = gson.toJson(consumerList)

                    // Emit success with JSON data and the object list
                    _rateStateFlow.emit(
                        RateViewState.SuccessOnlineRateB(jsonData, consumerList)
                    )
                }
                .flowOn(Dispatchers.IO)
                .launchIn(viewModelScope)
        }
    }


    fun getWRCOnlineList() {
        viewModelScope.launch {
            rateRepository.getAllWRC()
                .onStart {
                    _rateStateFlow.emit(RateViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                    CommonLogger.sysLogE("LOGzERROR", exception.localizedMessage, exception)
                }
                .onEach { consumerList ->
                    // Convert the list to JSON
                    val gson = Gson()
                    val jsonData = gson.toJson(consumerList)

                    // Emit success with JSON data and the object list
                    _rateStateFlow.emit(
                        RateViewState.SuccessOnlineRateC(jsonData, consumerList)
                    )
                }
                .flowOn(Dispatchers.IO)
                .launchIn(viewModelScope)
        }
    }

    fun getWRReOnlineList() {
        viewModelScope.launch {
            rateRepository.getAllWRRe()
                .onStart {
                    _rateStateFlow.emit(RateViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                    CommonLogger.sysLogE("LOGzERROR", exception.localizedMessage, exception)
                }
                .onEach { consumerList ->
                    // Convert the list to JSON
                    val gson = Gson()
                    val jsonData = gson.toJson(consumerList)

                    // Emit success with JSON data and the object list
                    _rateStateFlow.emit(
                        RateViewState.SuccessOnlineRateRe(jsonData, consumerList)
                    )
                }
                .flowOn(Dispatchers.IO)
                .launchIn(viewModelScope)
        }
    }
    private suspend fun onError(exception: Throwable) {
        when (exception) {
            is IOException,
            is TimeoutException,
            -> {
                _rateStateFlow.emit(
                    RateViewState.PopupError(
                        PopupErrorState.NetworkError
                    )
                )
            }

            is HttpException -> {
                val errorBody = exception.response()?.errorBody()
                val gson = Gson()
                val type = object : TypeToken<ErrorModel>() {}.type
                var errorResponse: ErrorModel? = gson.fromJson(errorBody?.charStream(), type)
                _rateStateFlow.emit(
                    RateViewState.PopupError(
                        if (AppConstant.isSessionStatusCode(errorResponse?.status_code.orEmpty())) {
                            PopupErrorState.SessionError
                        } else {
                            PopupErrorState.HttpError
                        }, errorResponse?.msg.orEmpty()
                    )
                )
            }

            else -> _rateStateFlow.emit(
                RateViewState.PopupError(
                    PopupErrorState.UnknownError
                )
            )
        }
    }
}
