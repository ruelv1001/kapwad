package com.lionscare.app.ui.onboarding.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lionscare.app.data.repositories.auth.AuthRepository
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
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {

    private val _splashStateFlow = MutableStateFlow<SplashViewState>(SplashViewState.Idle)

    val splashStateFlow: StateFlow<SplashViewState> = _splashStateFlow.asStateFlow()

    fun doRefreshToken() {
        viewModelScope.launch {
            authRepository.doRefreshToken()
                .onStart {
                    _splashStateFlow.emit(SplashViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _splashStateFlow.emit(
                        SplashViewState.SuccessRefreshToken(it.status ?: false)
                    )
                }
        }
    }

    private suspend fun onError(exception: Throwable) {
        when (exception) {
            is IOException,
            is TimeoutException
            -> {
                _splashStateFlow.emit(
                    SplashViewState.PopupError(
                        PopupErrorState.NetworkError
                    )
                )
            }
            is HttpException -> {
                val errorBody = exception.response()?.errorBody()
                val gson = Gson()
                val type = object : TypeToken<com.lionscare.app.data.model.ErrorModel>() {}.type
                var errorResponse: com.lionscare.app.data.model.ErrorModel? = gson.fromJson(errorBody?.charStream(), type)
                _splashStateFlow.emit(
                    SplashViewState.PopupError(
                        PopupErrorState.HttpError, errorResponse?.msg.orEmpty()
                    )
                )
            }
            else -> _splashStateFlow.emit(
                SplashViewState.PopupError(
                    PopupErrorState.UnknownError
                )
            )
        }
    }

}