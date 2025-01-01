package kapwad.reader.app.ui.crops.viemodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kapwad.reader.app.data.model.ErrorModel
import kapwad.reader.app.data.repositories.crops.CropsRepository
import kapwad.reader.app.data.repositories.crops.request.CropDetailsRequest
import kapwad.reader.app.data.repositories.crops.request.UploadImageRequest
import kapwad.reader.app.data.repositories.crops.request.UploadVideRequest
import kapwad.reader.app.security.AuthEncryptedDataManager
import kapwad.reader.app.utils.AppConstant
import kapwad.reader.app.utils.CommonLogger
import kapwad.reader.app.utils.PopupErrorState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeoutException
import javax.inject.Inject

@HiltViewModel
class CropsViewModel @Inject constructor(
    private val cropsRepository: CropsRepository,
    private val encryptedDataManager: AuthEncryptedDataManager
) : ViewModel() {

    private val _cropsSharedFlow = MutableSharedFlow<CropsViewState>()
    val user = encryptedDataManager.getUserBasicInfo()

    val cropsSharedFlow: SharedFlow<CropsViewState> =
        _cropsSharedFlow.asSharedFlow()


    var video: File? = null
    var image: File? = null

    fun getKycStatus(): String {
        return encryptedDataManager.getKYCStatus()
    }


    fun getCropsList() {
        viewModelScope.launch {
            cropsRepository.getAllCrops()
                .onStart {
                    _cropsSharedFlow.emit(CropsViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                    CommonLogger.sysLogE("LOGzERROR", exception.localizedMessage, exception)
                }
                .onEach {

                    _cropsSharedFlow.emit(
                        CropsViewState.Success(it.msg.orEmpty(), it)
                    )
                }
                .flowOn(Dispatchers.IO)
                .launchIn(viewModelScope)
        }
    }

    fun getCropsItemList(cropsId:String) {
        viewModelScope.launch {
            cropsRepository.getAllCropsItem(cropsId)
                .onStart {
                    _cropsSharedFlow.emit(CropsViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                    CommonLogger.sysLogE("LOGzERROR", exception.localizedMessage, exception)
                }
                .onEach {

                    _cropsSharedFlow.emit(
                        CropsViewState.SuccessCropItem(it.msg.orEmpty(), it)
                    )
                }
                .flowOn(Dispatchers.IO)
                .launchIn(viewModelScope)
        }
    }

    fun doUploadVideo(uploadVideRequest: UploadVideRequest) {
        viewModelScope.launch {
            cropsRepository.doUploadVideo(uploadVideRequest)
                .onStart {
                    _cropsSharedFlow.emit(CropsViewState.Loading)
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
                    _cropsSharedFlow.emit(CropsViewState.SuccessUploadVideo(it.msg.orEmpty()))
                }
        }
    }

    fun doUploadImage(uploadVideRequest: UploadImageRequest) {
        viewModelScope.launch {
            cropsRepository.doUploadImage(uploadVideRequest)
                .onStart {
                    _cropsSharedFlow.emit(CropsViewState.Loading)
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
                    _cropsSharedFlow.emit(CropsViewState.SuccessUploadImage(it.msg.orEmpty()))
                }
        }
    }

    fun doStartCrop() {
        viewModelScope.launch {
            cropsRepository.getStartCrop()
                .onStart {
                    _cropsSharedFlow.emit(CropsViewState.Loading)
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
                    _cropsSharedFlow.emit(CropsViewState.SuccessStart(it.msg.orEmpty()))
                }
        }
    }

    fun getCropDetails(
        cropDetailsRequest: CropDetailsRequest

    ) {
        viewModelScope.launch {
            cropsRepository.dogetCropDetails(
                cropDetailsRequest
            )
                .onStart {
                    _cropsSharedFlow.emit(CropsViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)

                }
                .collect {
                    _cropsSharedFlow.emit(
                        CropsViewState.SuccessCropDetails(it.msg.orEmpty(), it)
                    )
                }
        }
    }


//    private suspend fun getCropsList(perPage: Int) {
//        val pageConfig = PagingConfig(pageSize = perPage, initialLoadSize = perPage,enablePlaceholders = false)
//        cropsRepository.getAllCrops(pageConfig)
//            .cachedIn(viewModelScope)
//            .onStart {
//                _cropsSharedFlow.emit(CropsViewState.Loading)
//            }
//            .catch { exception ->
//                onError(exception)
//                CommonLogger.sysLogE("error",exception.localizedMessage, exception)
//            }
//            .collect { pagingData ->
//                _cropsSharedFlow.emit(
//                    CropsViewState.SuccessCropsList(pagingData))
//            }
//    }

//    fun loadCropsList(perPage: Int = 10) {
//        viewModelScope.launch {
//            getCropsList(perPage)
//        }
//    }

    private suspend fun onError(exception: Throwable) {
        when (exception) {
            is IOException,
            is TimeoutException,
            -> {
                _cropsSharedFlow.emit(
                    CropsViewState.PopupError(
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
                    _cropsSharedFlow.emit(CropsViewState.InputError(errorResponse.errors))
                } else {
                    _cropsSharedFlow.emit(
                        CropsViewState.PopupError(
                            if (AppConstant.isSessionStatusCode(errorResponse?.status_code.orEmpty())) {
                                PopupErrorState.SessionError
                            } else {
                                PopupErrorState.HttpError
                            }, errorResponse?.msg.orEmpty()
                        )
                    )
                }

            }

            else -> _cropsSharedFlow.emit(
                CropsViewState.PopupError(
                    PopupErrorState.UnknownError
                )
            )
        }
    }
}