package com.lionscare.app.ui.profile.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lionscare.app.utils.PopupErrorState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lionscare.app.data.repositories.baseresponse.UserModel
import com.lionscare.app.data.repositories.profile.ProfileRepository
import com.lionscare.app.data.repositories.profile.request.ProfileAvatarRequest
import com.lionscare.app.data.repositories.profile.request.UpdateInfoRequest
import com.lionscare.app.data.repositories.profile.request.UpdatePhoneNumberOTPRequest
import com.lionscare.app.data.repositories.profile.request.UpdatePhoneNumberRequest
import com.lionscare.app.ui.onboarding.viewmodel.LoginViewState
import com.lionscare.app.utils.AppConstant
import com.lionscare.app.utils.CommonLogger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeoutException
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _profileSharedFlow = MutableSharedFlow<ProfileViewState>()
    val profileSharedFlow: SharedFlow<ProfileViewState> =
        _profileSharedFlow.asSharedFlow()

    //holder for userModel
    var userModel : UserModel? = null

    //holder for phonenumber
    var phoneNumber : String? = null
    var lcRegionCode : String = ""
    var lcZoneCode : String = ""
    var lcClusterCode : String = ""

    //holder for avatar
    var avatarFileHolder : File? = null
    var avatarURIHolder : Uri? = null

    //holder for email add
    var email: String? = null

    fun changePhoneNumber(request : UpdatePhoneNumberRequest) {
        viewModelScope.launch {
            profileRepository.changePhoneNumber(request)
                .onStart {
                    _profileSharedFlow.emit(ProfileViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)

                }
                .collect {
                    _profileSharedFlow.emit(
                        ProfileViewState.SuccessUpdatePhoneNumber(it)
                    )
                }
        }
    }

    fun changePhoneNumberWithOTP(request : UpdatePhoneNumberOTPRequest) {
        viewModelScope.launch {
            profileRepository.changePhoneNumberWithOTP(request)
                .onStart {
                    _profileSharedFlow.emit(ProfileViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)

                }
                .collect {
                    _profileSharedFlow.emit(
                        ProfileViewState.SuccessUpdatePhoneNumberWithOTP(it)
                    )
                }
        }
    }

    fun getProfileDetails() {
        viewModelScope.launch {
            profileRepository.getProfileInfo()
                .onStart {
                    _profileSharedFlow.emit(ProfileViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)

                }
                .collect {
                    _profileSharedFlow.emit(
                        ProfileViewState.SuccessGetUserInfo(it.msg.orEmpty(),it.data)
                    )
                }
        }
    }



    fun doUpdateProfile(request : UpdateInfoRequest ) {
        viewModelScope.launch {
            profileRepository.doUpdateInfo(request)
                .onStart {
                    _profileSharedFlow.emit(ProfileViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)

                }
                .collect {
                    _profileSharedFlow.emit(
                        ProfileViewState.Success(it.msg.orEmpty())
                    )
                }
        }
    }

    fun uploadAvatar(request : ProfileAvatarRequest ) {
        viewModelScope.launch {
            profileRepository.uploadAvatar(request)
                .onStart {
                    _profileSharedFlow.emit(ProfileViewState.LoadingAvatar)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _profileSharedFlow.emit(
                        ProfileViewState.SuccessUploadAvatar(it.msg.orEmpty())
                    )
                }
        }
    }




    private suspend fun onError(exception: Throwable) {
        when (exception) {
            is IOException,
            is TimeoutException,
            -> {
                _profileSharedFlow.emit(
                    ProfileViewState.PopupError(
                        PopupErrorState.NetworkError
                    )
                )
            }
            is HttpException -> {
                val errorBody = exception.response()?.errorBody()
                val gson = Gson()
                val type = object : TypeToken<com.lionscare.app.data.model.ErrorModel>() {}.type
                var errorResponse: com.lionscare.app.data.model.ErrorModel? = gson.fromJson(errorBody?.charStream(), type)
                if (errorResponse?.has_requirements == true) {
                    _profileSharedFlow.emit(ProfileViewState.InputError(errorResponse.errors))
                } else {
                    _profileSharedFlow.emit(
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
            else -> _profileSharedFlow.emit(
                ProfileViewState.PopupError(
                    PopupErrorState.UnknownError
                )
            )
        }
    }

}