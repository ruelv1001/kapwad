package kapwad.reader.app.ui.geotagging.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kapwad.reader.app.data.model.ErrorModel

import kapwad.reader.app.data.repositories.geotagging.GeotaggingRepository
import kapwad.reader.app.data.repositories.geotagging.request.GeotaggingUploadRequest
import kapwad.reader.app.security.AuthEncryptedDataManager
import kapwad.reader.app.utils.AppConstant
import kapwad.reader.app.utils.CommonLogger
import kapwad.reader.app.utils.PopupErrorState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeoutException
import javax.inject.Inject

@HiltViewModel
class GeoTaggingViewModel @Inject constructor(
    private val geotaggingRepository: GeotaggingRepository,
    private val encryptedDataManager: AuthEncryptedDataManager
) : ViewModel() {

    private val _geotaggingSharedFlow = MutableSharedFlow<GeoTaggingViewState>()
    val user = encryptedDataManager.getUserBasicInfo()

    val geotaggingSharedFlow: SharedFlow<GeoTaggingViewState> =
        _geotaggingSharedFlow.asSharedFlow()


    var image: File? = null


    fun doUploadImageArea(geotaggingRequest: GeotaggingUploadRequest) {
        viewModelScope.launch {
            geotaggingRepository.doUploadImage(geotaggingRequest)
                .onStart {
                    _geotaggingSharedFlow.emit(GeoTaggingViewState.Loading)
                }
                .catch { exception ->
                    CommonLogger.instance.sysLogE(
                        "",
                        exception.localizedMessage,
                        exception
                    )
                    onError(exception)
                }
                .collect {
                    _geotaggingSharedFlow.emit(GeoTaggingViewState.SuccessUploadImage(it.msg.orEmpty()))
                }
        }
    }


    fun getStatus(


    ) {
        viewModelScope.launch {
            geotaggingRepository.doGetStatus(

            )
                .onStart {
                    _geotaggingSharedFlow.emit(GeoTaggingViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)

                }
                .collect {
                    _geotaggingSharedFlow.emit(
                        GeoTaggingViewState.SuccessStatus(it.msg.orEmpty(), it)
                    )
                }
        }
    }


    private suspend fun onError(exception: Throwable) {
        when (exception) {
            is IOException,
            is TimeoutException,
            -> {
                _geotaggingSharedFlow.emit(
                    GeoTaggingViewState.PopupError(
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
                    _geotaggingSharedFlow.emit(GeoTaggingViewState.InputError(errorResponse.errors))
                } else {
                    _geotaggingSharedFlow.emit(
                        GeoTaggingViewState.PopupError(
                            if (AppConstant.isSessionStatusCode(errorResponse?.status_code.orEmpty())) {
                                PopupErrorState.SessionError
                            } else {
                                PopupErrorState.HttpError
                            }, errorResponse?.msg.orEmpty()
                        )
                    )
                }

            }

            else -> _geotaggingSharedFlow.emit(
                GeoTaggingViewState.PopupError(
                    PopupErrorState.UnknownError
                )
            )
        }
    }
}