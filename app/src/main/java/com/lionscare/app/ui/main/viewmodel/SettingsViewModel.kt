package com.lionscare.app.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lionscare.app.data.repositories.auth.AuthRepository
import com.lionscare.app.utils.CommonLogger
import com.lionscare.app.utils.PopupErrorState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeoutException
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginSharedFlow = MutableSharedFlow<SettingsViewState>()

    val loginSharedFlow = _loginSharedFlow.asSharedFlow()

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

    fun getUserInfo() {
        viewModelScope.launch {
            authRepository.getUserInfo()
                .onStart {
                    _loginSharedFlow.emit(SettingsViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                    CommonLogger.instance.sysLogE(
                        "LoginViewModel",
                        exception.localizedMessage,
                        exception
                    )
                }
                .collect {
                    _loginSharedFlow.emit(
                        SettingsViewState.SuccessGetUserInfo(it)
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
                    _loginSharedFlow.emit(
                        SettingsViewState.PopupError(
                            PopupErrorState.HttpError, errorResponse?.msg.orEmpty()
                        )
                    )
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