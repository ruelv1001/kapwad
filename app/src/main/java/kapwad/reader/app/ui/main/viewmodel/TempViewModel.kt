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
import kapwad.reader.app.data.model.TempListModelData

import kapwad.reader.app.data.repositories.temp.TempRepository
import kapwad.reader.app.ui.main.viewmodel.ConsumerViewState
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
class TempViewModel @Inject constructor(
    private val tempRepository: TempRepository,
    authEncryptedDataManager: AuthEncryptedDataManager
) : ViewModel() {

    private val _tempStateFlow = MutableStateFlow<TempViewState>(TempViewState.Idle)
    val tempStateFlow: StateFlow<TempViewState> = _tempStateFlow.asStateFlow()


    fun insertTemp(data: List<TempListModelData>) {
        viewModelScope.launch {
            tempRepository.create(data)
                .onStart { _tempStateFlow.emit(TempViewState.Loading) }
                .catch { exception -> onError(exception) }
                .collect { _tempStateFlow.emit(TempViewState.SuccessOfflineCreate(it)) }
        }
    }



    fun getTemp() {
        viewModelScope.launch {
            tempRepository.getTemp()
                .onStart {
                    _tempStateFlow.emit(TempViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _tempStateFlow.emit(TempViewState.SuccessOfflineGetOrder(it))
                }
        }
    }




    fun deleteAllTemp() {
        viewModelScope.launch {
            tempRepository.deleteAllTemp()
                .onStart {
                    _tempStateFlow.emit(TempViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _tempStateFlow.emit(TempViewState.SuccessDelete(it.toString()))
                }
        }
    }

    fun getTempOnlineList() {
        viewModelScope.launch {
            tempRepository.getAllTemp()
                .onStart {
                    _tempStateFlow.emit(TempViewState.Loading)
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
                    _tempStateFlow.emit(
                        TempViewState.SuccessOnlineTemp(jsonData, consumerList)
                    )
                }
                .flowOn(Dispatchers.IO)
                .launchIn(viewModelScope)
        }
    }


    fun getTempById(id: String) {
        viewModelScope.launch {
            tempRepository.getTempById(id)
                .onStart {
                    _tempStateFlow.emit(TempViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect { response ->
                    Log.d("GetConsumerById", "Response: $response")
                    _tempStateFlow.emit(TempViewState.SuccessTempById(response))
                }
        }
    }

    private suspend fun onError(exception: Throwable) {
        when (exception) {
            is IOException,
            is TimeoutException,
            -> {
                _tempStateFlow.emit(
                    TempViewState.PopupError(
                        PopupErrorState.NetworkError
                    )
                )
            }

            is HttpException -> {
                val errorBody = exception.response()?.errorBody()
                val gson = Gson()
                val type = object : TypeToken<ErrorModel>() {}.type
                var errorResponse: ErrorModel? = gson.fromJson(errorBody?.charStream(), type)
                _tempStateFlow.emit(
                    TempViewState.PopupError(
                        if (AppConstant.isSessionStatusCode(errorResponse?.status_code.orEmpty())) {
                            PopupErrorState.SessionError
                        } else {
                            PopupErrorState.HttpError
                        }, errorResponse?.msg.orEmpty()
                    )
                )
            }

            else -> _tempStateFlow.emit(
                TempViewState.PopupError(
                    PopupErrorState.UnknownError
                )
            )
        }
    }
}
