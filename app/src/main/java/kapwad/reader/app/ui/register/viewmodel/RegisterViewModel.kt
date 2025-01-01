package kapwad.reader.app.ui.register.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kapwad.reader.app.utils.PopupErrorState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kapwad.reader.app.data.repositories.profile.ProfileRepository
import kapwad.reader.app.data.repositories.profile.request.UpdateInfoRequest
import kapwad.reader.app.data.repositories.registration.RegistrationRepository
import kapwad.reader.app.data.repositories.registration.request.OTPRequest
import kapwad.reader.app.data.repositories.registration.request.RegistrationRequest
import kapwad.reader.app.utils.AppConstant
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

    //default values
    var countryCode = "+63"
    var countryIso = "PH"

    private val _registerSharedFlow = MutableSharedFlow<RegisterViewState>()

    val registerSharedFlow: SharedFlow<RegisterViewState> =
        _registerSharedFlow.asSharedFlow()

    fun doPreReg(preRegRequest: RegistrationRequest) {
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

    fun doRequestOTP(code: OTPRequest) {
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

    fun doReg(preRegRequest: RegistrationRequest) {
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

    fun doUpdateProfile(request: UpdateInfoRequest) {
        viewModelScope.launch {
            profileRepository.doUpdateInfo(request)
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

    fun doScanQR(code: String) {
        viewModelScope.launch {
            regRepository.doScanQR(code)
                .onStart {
                    _registerSharedFlow.emit(RegisterViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)

                }
                .collect {
                    _registerSharedFlow.emit(
                        RegisterViewState.SuccessScanQR(it.msg.orEmpty(), it)
                    )
                }
        }
    }

    fun doPrevalidatePassword(password: String, passwordConfirmation: String) {
        viewModelScope.launch {
            regRepository.doPrevalidatePassword(
                password = password,
                passwordConfirmation = passwordConfirmation
            )
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

    fun doRequestOnboardingOTP(code: String) {
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

    fun doValidateAndSetPassword(
        code: String,
        password: String,
        passwordConfirmation: String,
        otp: String
    ) {
        viewModelScope.launch {
            regRepository.doValidateAndSetPassword(
                code = code,
                password = password,
                passwordConfirmation = passwordConfirmation,
                otp = otp
            )
                .onStart {
                    _registerSharedFlow.emit(RegisterViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)

                }
                .collect {
                    _registerSharedFlow.emit(
                        RegisterViewState.SuccessValidateAndSetPassword(it.msg.orEmpty(), it)
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
                val type = object : TypeToken<kapwad.reader.app.data.model.ErrorModel>() {}.type
                var errorResponse: kapwad.reader.app.data.model.ErrorModel? =
                    gson.fromJson(errorBody?.charStream(), type)
                if (errorResponse?.has_requirements == true) {
                    _registerSharedFlow.emit(RegisterViewState.InputError(errorResponse.errors))
                } else {
                    _registerSharedFlow.emit(
                        RegisterViewState.PopupError(
                            if (AppConstant.isSessionStatusCode(errorResponse?.status_code.orEmpty())) {
                                PopupErrorState.SessionError
                            } else {
                                PopupErrorState.HttpError
                            }, errorResponse?.msg.orEmpty()
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