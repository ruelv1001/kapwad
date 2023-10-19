package com.lionscare.app.ui.verify

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lionscare.app.data.model.ErrorModel
import com.lionscare.app.data.repositories.profile.ProfileRepository
import com.lionscare.app.data.repositories.profile.request.FaceIDRequest
import com.lionscare.app.data.repositories.profile.request.KYCRequest
import com.lionscare.app.security.AuthEncryptedDataManager
import com.lionscare.app.ui.profile.viewmodel.ProfileViewState
import com.lionscare.app.ui.register.viewmodel.AddressViewState
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
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeoutException
import javax.inject.Inject

@HiltViewModel
class VerifyViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val authEncryptedDataManager: AuthEncryptedDataManager
) : ViewModel() {
    private val _kycSharedFlow = MutableSharedFlow<ProfileViewState>()

    val kycSharedFlow: SharedFlow<ProfileViewState> =
        _kycSharedFlow.asSharedFlow()

    var frontImageFile: File? = null
    var backImageFile: File? = null
    var leftImageFile: File? = null
    var rightImageFile: File? = null

    fun updateKYCStatus(value: String){
        authEncryptedDataManager.setUserKYCStatus(value)
    }

    fun doUploadId(kycRequest: KYCRequest) {
        viewModelScope.launch {
            profileRepository.doUploadId(kycRequest)
                .onStart {
                    _kycSharedFlow.emit(ProfileViewState.Loading)
                }
                .catch { exception ->
                    CommonLogger.instance.sysLogE(
                        "KYCViewModel",
                        exception.localizedMessage,
                        exception
                    )
                    onError(exception)
                }
                .collect {
                    _kycSharedFlow.emit(ProfileViewState.SuccessUploadId(it.msg.orEmpty()))
                }
        }
    }

    fun doUploadProofOfAddress(kycRequest : KYCRequest) {
        viewModelScope.launch {
            profileRepository.doUploadProofAddress(kycRequest)
                .onStart {
                    _kycSharedFlow.emit(ProfileViewState.Loading)
                }
                .catch { exception ->
                    CommonLogger.instance.sysLogE(
                        "KYCViewModel",
                        exception.localizedMessage,
                        exception
                    )
                    onError(exception)
                }
                .collect {
                    _kycSharedFlow.emit(ProfileViewState.SuccessUploadAddress(it.msg.orEmpty()))
                }
        }
    }

    fun doUploadFacialIds(kycRequest : FaceIDRequest) {
        viewModelScope.launch {
            profileRepository.doUploadFacialId(kycRequest)
                .onStart {
                    _kycSharedFlow.emit(ProfileViewState.Loading)
                }
                .catch { exception ->
                    CommonLogger.instance.sysLogE(
                        "KYCViewModel",
                        exception.localizedMessage,
                        exception
                    )
                    onError(exception)
                }
                .collect {
                    _kycSharedFlow.emit(ProfileViewState.SuccessUploadFacialId(it.msg.orEmpty()))
                }
        }
    }


    fun getIDLists(){
        viewModelScope.launch {
            profileRepository.getIdList()
                .onStart {
                    _kycSharedFlow.emit(ProfileViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _kycSharedFlow.emit(ProfileViewState.SuccessLoadLOVIds(message = it.msg.toString(), lovResponse = it))
                }
        }
    }

    fun getProofOfAddressLists() {
        viewModelScope.launch {
            profileRepository.getProofOfAddressList()
                .onStart {
                    _kycSharedFlow.emit(ProfileViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _kycSharedFlow.emit(ProfileViewState.SuccessLoadLOVProofOfAddress(message = it.msg.toString(), lovResponse = it))
                }
        }
    }

    fun getVerificationStatus() {
        viewModelScope.launch {
            profileRepository.getVerificationStatus()
                .onStart {
                    _kycSharedFlow.emit(ProfileViewState.LoadingVerificationStatus)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _kycSharedFlow.emit(ProfileViewState.SuccessGetVerificationStatus(message = it.msg.toString(), profileVerificationResponse = it))
                }
        }
    }


    private suspend fun onError(exception: Throwable) {
        when (exception) {
            is IOException,
            is TimeoutException
            -> {
                _kycSharedFlow.emit(
                    ProfileViewState.PopupError(
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
                    _kycSharedFlow.emit(ProfileViewState.InputError(errorResponse.errors))
                } else {
                    _kycSharedFlow.emit(
                        ProfileViewState.PopupError(
                            if (AppConstant.isSessionStatusCode(errorResponse?.status_code.orEmpty())){
                                PopupErrorState.SessionError
                            }else{
                                PopupErrorState.HttpError
                            }
                            , errorResponse?.msg.orEmpty()
                        )
                    )
                }
            }
            else -> _kycSharedFlow.emit(
                ProfileViewState.PopupError(
                    PopupErrorState.UnknownError
                )
            )
        }
    }
}