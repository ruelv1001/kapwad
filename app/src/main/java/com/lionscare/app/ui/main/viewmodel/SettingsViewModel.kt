package com.lionscare.app.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lionscare.app.data.repositories.auth.AuthRepository
import com.lionscare.app.utils.PopupErrorState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lionscare.app.data.repositories.profile.ProfileRepository
import com.lionscare.app.ui.profile.viewmodel.ProfileViewState
import com.lionscare.app.utils.AppConstant
import com.lionscare.app.utils.AppConstant.NOT_FOUND
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeoutException
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor (
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _loginSharedFlow = MutableSharedFlow<SettingsViewState>()

    val loginSharedFlow = _loginSharedFlow.asSharedFlow()

    var userQrCode = ""
    fun doLogoutAccount() {
        viewModelScope.launch {
            authRepository.doLogout()
                .onStart {
                    _loginSharedFlow.emit(SettingsViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)

                }
                .collect {
                    _loginSharedFlow.emit(
                        SettingsViewState.Success(it.msg.orEmpty())
                    )
                }
        }
    }

    fun getProfileDetails() {
        viewModelScope.launch {
            profileRepository.getProfileInfo()
                .onStart {
                    _loginSharedFlow.emit(SettingsViewState.LoadingProfile)
                }
                .catch { exception ->
                    onError(exception)

                }
                .collect {
                    _loginSharedFlow.emit(
                        SettingsViewState.SuccessGetUserInfo(it.msg.orEmpty(),it.data)
                    )
                }
        }
    }

    fun getBadgeStatus() {
        viewModelScope.launch {
            profileRepository.getBadgeStatus()
                .onStart {
                    _loginSharedFlow.emit(SettingsViewState.LoadingBadge)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _loginSharedFlow.emit(
                        SettingsViewState.SuccessGetBadgeStatus(
                         badgeStatus = it.data)
                    )
                }
        }
    }



    fun doChangePass(
        currentPass: String,
        newPass: String,
        confirmPass: String
    ) {
        viewModelScope.launch {
            profileRepository.doChangePass(currentPass, newPass, confirmPass)
                .onStart {
                    _loginSharedFlow.emit(SettingsViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)

                }
                .collect {
                    _loginSharedFlow.emit(
                        SettingsViewState.Success(it.msg.orEmpty())
                    )
                }
        }
    }

    private suspend fun onError(exception: Throwable) {
        when (exception) {
            is IOException,
            is TimeoutException,
            -> {
                _loginSharedFlow.emit(
                    SettingsViewState.PopupError(
                        PopupErrorState.NetworkError
                    )
                )
            }

            is HttpException -> {
                val errorBody = exception.response()?.errorBody()
                val gson = Gson()
                val type = object : TypeToken<com.lionscare.app.data.model.ErrorModel>() {}.type
                var errorResponse: com.lionscare.app.data.model.ErrorModel? =
                    gson.fromJson(errorBody?.charStream(), type)
                if (errorResponse?.has_requirements == true) {
                    _loginSharedFlow.emit(SettingsViewState.InputError(errorResponse.errors))
                } else {
                    if (errorResponse?.status_code.orEmpty() != NOT_FOUND){
                        _loginSharedFlow.emit(
                            SettingsViewState.PopupError(
                                PopupErrorState.HttpError,
                                errorResponse?.msg.orEmpty()
                            )
                        )
                    }else if(AppConstant.isSessionStatusCode(errorResponse?.status_code.orEmpty())){
                        _loginSharedFlow.emit(
                            SettingsViewState.PopupError(
                                PopupErrorState.SessionError,
                                errorResponse?.msg.orEmpty()
                            )
                        )
                    }
                }
            }

            else -> _loginSharedFlow.emit(
                SettingsViewState.PopupError(
                    PopupErrorState.UnknownError
                )
            )
        }
    }

}