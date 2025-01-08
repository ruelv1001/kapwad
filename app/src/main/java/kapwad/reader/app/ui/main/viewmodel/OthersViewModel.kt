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
import kapwad.reader.app.data.model.OtherListModelData
import kapwad.reader.app.data.repositories.others.OthersRepository


import kapwad.reader.app.ui.main.viewmodel.ConsumerViewState
import kapwad.reader.app.ui.main.viewmodel.OthersViewState



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
class OthersViewModel @Inject constructor(
    private val othersRepository: OthersRepository,
    authEncryptedDataManager: AuthEncryptedDataManager
) : ViewModel() {

    private val _otherStateFlow = MutableStateFlow<OthersViewState>(OthersViewState.Idle)
    val otherStateFlow: StateFlow<OthersViewState> = _otherStateFlow.asStateFlow()


    fun insertOther(data: List<OtherListModelData>) {
        viewModelScope.launch {
            othersRepository.create(data)
                .onStart { _otherStateFlow.emit(OthersViewState.Loading) }
                .catch { exception -> onError(exception) }
                .collect { _otherStateFlow.emit(OthersViewState.SuccessOfflineCreate(it)) }
        }
    }



    fun getOther() {
        viewModelScope.launch {
            othersRepository.getOther()
                .onStart {
                    _otherStateFlow.emit(OthersViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _otherStateFlow.emit(OthersViewState.SuccessOfflineGetOrder(it))
                }
        }
    }




    fun deleteAllTemp() {
        viewModelScope.launch {
            othersRepository.deleteAllOther()
                .onStart {
                    _otherStateFlow.emit(OthersViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _otherStateFlow.emit(OthersViewState.SuccessDelete(it.toString()))
                }
        }
    }

    fun getOtherOnlineList() {
        viewModelScope.launch {
            othersRepository.getAllOther()
                .onStart {
                    _otherStateFlow.emit(OthersViewState.Loading)
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
                    _otherStateFlow.emit(
                        OthersViewState.SuccessOnlineOther(jsonData, consumerList)
                    )
                }
                .flowOn(Dispatchers.IO)
                .launchIn(viewModelScope)
        }
    }


    fun getTempById(id: String) {
        viewModelScope.launch {
            othersRepository.getOtherById(id)
                .onStart {
                    _otherStateFlow.emit(OthersViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect { response ->
                    Log.d("GetConsumerById", "Response: $response")
                    _otherStateFlow.emit(OthersViewState.SuccessOtherById(response))
                }
        }
    }

    private suspend fun onError(exception: Throwable) {
        when (exception) {
            is IOException,
            is TimeoutException,
            -> {
                _otherStateFlow.emit(
                    OthersViewState.PopupError(
                        PopupErrorState.NetworkError
                    )
                )
            }

            is HttpException -> {
                val errorBody = exception.response()?.errorBody()
                val gson = Gson()
                val type = object : TypeToken<ErrorModel>() {}.type
                var errorResponse: ErrorModel? = gson.fromJson(errorBody?.charStream(), type)
                _otherStateFlow.emit(
                    OthersViewState.PopupError(
                        if (AppConstant.isSessionStatusCode(errorResponse?.status_code.orEmpty())) {
                            PopupErrorState.SessionError
                        } else {
                            PopupErrorState.HttpError
                        }, errorResponse?.msg.orEmpty()
                    )
                )
            }

            else -> _otherStateFlow.emit(
                OthersViewState.PopupError(
                    PopupErrorState.UnknownError
                )
            )
        }
    }
}
