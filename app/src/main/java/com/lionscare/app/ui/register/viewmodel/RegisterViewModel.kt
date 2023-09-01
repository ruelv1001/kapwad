package com.lionscare.app.ui.register.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lionscare.app.data.repositories.auth.AuthRepository
import com.lionscare.app.utils.CommonLogger
import com.lionscare.app.utils.PopupErrorState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lionscare.app.data.repositories.profile.ProfileRepository
import com.lionscare.app.data.repositories.registration.RegistrationRepository
import com.lionscare.app.data.repositories.registration.request.OTPRequest
import com.lionscare.app.data.repositories.registration.request.RegistrationRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeoutException
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val regRepository: RegistrationRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _registerSharedFlow = MutableSharedFlow<RegisterViewState>()

    val registerSharedFlow: SharedFlow<RegisterViewState> =
        _registerSharedFlow.asSharedFlow()

    fun doPreReg(preRegRequest : RegistrationRequest) {
        viewModelScope.launch {
            regRepository.doValidateFields(preRegRequest)
                .onStart {
                    _registerSharedFlow.emit(RegisterViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)

                }
                .collect {
                    _registerSharedFlow.emit(
                        RegisterViewState.Success(it.msg.orEmpty())
                    )
                }
        }
    }

    fun doRequestOTP(code : OTPRequest) {
        viewModelScope.launch {
            regRepository.doRequestOTP(code)
                .onStart {
                    _registerSharedFlow.emit(RegisterViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)

                }
                .collect {
                    _registerSharedFlow.emit(
                        RegisterViewState.Success(it.msg.orEmpty())
                    )
                }
        }
    }

    fun doReg(preRegRequest : RegistrationRequest) {
        viewModelScope.launch {
            regRepository.doRegister(preRegRequest)
                .onStart {
                    _registerSharedFlow.emit(RegisterViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)

                }
                .collect {
                    _registerSharedFlow.emit(
                        RegisterViewState.SuccessReg(it.msg.orEmpty())
                    )
                }
        }
    }

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
                        email : String,
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
                    _registerSharedFlow.emit(RegisterViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)

                }
                .collect {
                    _registerSharedFlow.emit(
                        RegisterViewState.SuccessProfileUpdate(it.msg.orEmpty())
                    )
                }
        }
    }


    private suspend fun onError(exception: Throwable) {
        when (exception) {
            is IOException,
            is TimeoutException,
            -> {
                _registerSharedFlow.emit(
                    RegisterViewState.PopupError(
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
                    _registerSharedFlow.emit(RegisterViewState.InputError(errorResponse.errors))
                } else {
                    _registerSharedFlow.emit(
                        RegisterViewState.PopupError(
                            PopupErrorState.HttpError, errorResponse?.msg.orEmpty()
                        )
                    )
                }

            }
            else -> _registerSharedFlow.emit(
                RegisterViewState.PopupError(
                    PopupErrorState.UnknownError
                )
            )
        }
    }

}