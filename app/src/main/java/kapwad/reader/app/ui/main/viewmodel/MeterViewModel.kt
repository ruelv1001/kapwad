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
import kapwad.reader.app.data.local.UserLocalData
import kapwad.reader.app.data.model.ConsumerListModelData
import kapwad.reader.app.data.model.MeterReaderListModelData
import kapwad.reader.app.data.model.OtherListModelData
import kapwad.reader.app.data.repositories.auth.AuthLocalDataSource
import kapwad.reader.app.data.repositories.baseresponse.UserModel
import kapwad.reader.app.data.repositories.meter.MeterRepository



import kapwad.reader.app.ui.main.viewmodel.ConsumerViewState
import kapwad.reader.app.ui.main.viewmodel.MeterViewState




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
class MeterViewModel @Inject constructor(
    private val meterRepository: MeterRepository,
    authEncryptedDataManager: AuthEncryptedDataManager,
    private val encryptedDataManager: AuthEncryptedDataManager,
    private val authLocalDataSource: AuthLocalDataSource,
) : ViewModel() {

    private val _meterStateFlow = MutableStateFlow<MeterViewState>(MeterViewState.Idle)
    val meterStateFlow: StateFlow<MeterViewState> = _meterStateFlow.asStateFlow()


    fun insertMeter(data: List<MeterReaderListModelData>) {
        viewModelScope.launch {
            meterRepository.create(data)
                .onStart { _meterStateFlow.emit(MeterViewState.Loading) }
                .catch { exception -> onError(exception) }
                .collect { _meterStateFlow.emit(MeterViewState.SuccessOfflineCreate(it)) }
        }
    }



    fun getMeter() {
        viewModelScope.launch {
            meterRepository.getOther()
                .onStart {
                    _meterStateFlow.emit(MeterViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _meterStateFlow.emit(MeterViewState.SuccessOfflineGetOrder(it))
                }
        }
    }




    fun deleteAlMeter() {
        viewModelScope.launch {
            meterRepository.deleteAllMeter()
                .onStart {
                    _meterStateFlow.emit(MeterViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _meterStateFlow.emit(MeterViewState.SuccessDelete(it.toString()))
                }
        }
    }

    fun getMeterOnlineList() {
        viewModelScope.launch {
            meterRepository.getAllMeterOnline()
                .onStart {
                    _meterStateFlow.emit(MeterViewState.Loading)
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
                    _meterStateFlow.emit(
                        MeterViewState.SuccessOnlineMeter(jsonData, consumerList)
                    )
                }
                .flowOn(Dispatchers.IO)
                .launchIn(viewModelScope)
        }
    }


    fun getMeterById(id: String) {
        viewModelScope.launch {
            meterRepository.getMeterById(id)
                .onStart {
                    _meterStateFlow.emit(MeterViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect { response ->
                    Log.d("GetConsumerById", "Response: $response")
                    _meterStateFlow.emit(MeterViewState.SuccessOtherById(response))
                }
        }
    }

    fun getMeterByAccount(username: String, password: String) {
        viewModelScope.launch {
            meterRepository.getMeterByCredential(username, password)
                .onStart {
                    _meterStateFlow.emit(MeterViewState.Loading)
                }
                .catch { exception ->
                    onError(exception) // Handle exceptions
                }
                .collect { response ->
                    if (response != null) {
                        Log.d("GetConsumerById", "Response: $response")
                        _meterStateFlow.emit(MeterViewState.SuccessOtherById(response))
                        encryptedDataManager.setUserBasicInfo(response)

                    } else {
                        Log.d("GetConsumerById", "Response is null")
                        _meterStateFlow.emit(MeterViewState.Error("Invalid credentials."))
                    }
                }
        }
    }



    private suspend fun onError(exception: Throwable) {
        when (exception) {
            is IOException,
            is TimeoutException,
            -> {
                _meterStateFlow.emit(
                    MeterViewState.PopupError(
                        PopupErrorState.NetworkError
                    )
                )
            }

            is HttpException -> {
                val errorBody = exception.response()?.errorBody()
                val gson = Gson()
                val type = object : TypeToken<ErrorModel>() {}.type
                var errorResponse: ErrorModel? = gson.fromJson(errorBody?.charStream(), type)
                _meterStateFlow.emit(
                    MeterViewState.PopupError(
                        if (AppConstant.isSessionStatusCode(errorResponse?.status_code.orEmpty())) {
                            PopupErrorState.SessionError
                        } else {
                            PopupErrorState.HttpError
                        }, errorResponse?.msg.orEmpty()
                    )
                )
            }

            else -> _meterStateFlow.emit(
                MeterViewState.PopupError(
                    PopupErrorState.UnknownError
                )
            )
        }
    }
}
