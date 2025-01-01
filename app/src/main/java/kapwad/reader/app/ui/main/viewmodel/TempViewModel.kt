package kapwad.reader.app.data.viewmodels

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

import kapwad.reader.app.data.repositories.consumers.ConsumerRepository
import kapwad.reader.app.ui.main.viewmodel.ConsumerViewState
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
class ConsumerViewModel @Inject constructor(
    private val consumerRepository: ConsumerRepository,
    authEncryptedDataManager: AuthEncryptedDataManager
) : ViewModel() {

    private val _consumerStateFlow = MutableStateFlow<ConsumerViewState>(ConsumerViewState.Idle)
    val consumerStateFlow: StateFlow<ConsumerViewState> = _consumerStateFlow.asStateFlow()


    // Add or update a cart item
    fun insertConsumerOffline(consumerListModelData: ConsumerListModelData) {
        viewModelScope.launch {
            consumerRepository.createConsumer(consumerListModelData)
                .onStart {
                    _consumerStateFlow.emit(ConsumerViewState.Loading)
                }
                .catch { exception ->
                    // Handle the error
                    onError(exception)
                }
                .collect {
                    _consumerStateFlow.emit(ConsumerViewState.SuccessOfflineCreateOrder(it))
                }
        }
    }


    fun insertConsumer(consumerListModelData: ConsumerListModelData) {
        viewModelScope.launch {
            consumerRepository.createConsumer(consumerListModelData)
                .onStart {
                    _consumerStateFlow.emit(ConsumerViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _consumerStateFlow.emit(ConsumerViewState.SuccessOfflineCreateOrder(it))
                }
        }
    }

    fun getConsumer() {
        viewModelScope.launch {
            consumerRepository.getConsumer()
                .onStart {
                    _consumerStateFlow.emit(ConsumerViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _consumerStateFlow.emit(ConsumerViewState.SuccessOfflineGetOrder(it))
                }
        }
    }




    fun deleteAllConsumer() {
        viewModelScope.launch {
            consumerRepository.deleteAllConsumer()
                .onStart {
                    _consumerStateFlow.emit(ConsumerViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _consumerStateFlow.emit(ConsumerViewState.SuccessDelete(it.toString()))
                }
        }
    }

    fun getConsumerOnlineList() {
        viewModelScope.launch {
            consumerRepository.getAllConsumer()
                .onStart {
                    _consumerStateFlow.emit(ConsumerViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                    CommonLogger.sysLogE("LOGzERROR", exception.localizedMessage, exception)
                }
                .onEach {

                    _consumerStateFlow.emit(
                        ConsumerViewState.SuccessOnlineConsumer(it.toString(), it)
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
                _consumerStateFlow.emit(
                    ConsumerViewState.PopupError(
                        PopupErrorState.NetworkError
                    )
                )
            }

            is HttpException -> {
                val errorBody = exception.response()?.errorBody()
                val gson = Gson()
                val type = object : TypeToken<ErrorModel>() {}.type
                var errorResponse: ErrorModel? = gson.fromJson(errorBody?.charStream(), type)
                _consumerStateFlow.emit(
                    ConsumerViewState.PopupError(
                        if (AppConstant.isSessionStatusCode(errorResponse?.status_code.orEmpty())) {
                            PopupErrorState.SessionError
                        } else {
                            PopupErrorState.HttpError
                        }, errorResponse?.msg.orEmpty()
                    )
                )
            }

            else -> _consumerStateFlow.emit(
                ConsumerViewState.PopupError(
                    PopupErrorState.UnknownError
                )
            )
        }
    }
}
