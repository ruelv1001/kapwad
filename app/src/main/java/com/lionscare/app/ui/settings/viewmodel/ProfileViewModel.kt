package com.lionscare.app.ui.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lionscare.app.utils.PopupErrorState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lionscare.app.data.repositories.profile.ProfileRepository
import com.lionscare.app.data.repositories.profile.request.UpdatePhoneNumberOTPRequest
import com.lionscare.app.data.repositories.profile.request.UpdatePhoneNumberRequest
import com.lionscare.app.data.repositories.registration.RegistrationRepository
import com.lionscare.app.data.repositories.registration.request.OTPRequest
import com.lionscare.app.data.repositories.registration.request.RegistrationRequest
import com.lionscare.app.ui.register.viewmodel.RegisterViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeoutException
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
) : ViewModel() {

    private val _profileSharedFlow = MutableSharedFlow<ProfileViewState>()

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

    val profileSharedFlow: SharedFlow<ProfileViewState> =
        _profileSharedFlow.asSharedFlow()

    fun doUpdateProfile(province_sku: String,
                        province_name: String,
                        city_sku: String,
                        city_name: String,
                        brgy_sku: String,
                        brgy_name: String,
                        street_name: String,
                        zipcode: String,
                        firstname: String,
                        lastname: String,
                        middlename: String,
                        email:String
    ) {
        viewModelScope.launch {
            profileRepository.doUpdateInfo(
                province_sku,
                province_name,
                city_sku,
                city_name,
                brgy_sku,
                brgy_name,
                street_name,
                zipcode,
                firstname,
                lastname,
                middlename,
                email)
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
                            PopupErrorState.HttpError, errorResponse?.msg.orEmpty()
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