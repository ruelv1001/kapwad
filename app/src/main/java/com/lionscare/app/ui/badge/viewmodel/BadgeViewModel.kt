package com.lionscare.app.ui.badge.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lionscare.app.data.model.ErrorModel
import com.lionscare.app.data.repositories.profile.ProfileRepository
import com.lionscare.app.data.repositories.profile.request.BadgeRequest
import com.lionscare.app.ui.settings.viewmodel.ProfileViewState
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
import java.io.IOException
import java.util.concurrent.TimeoutException
import javax.inject.Inject

@HiltViewModel
class BadgeViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {
    private val _badgeSharedFlow = MutableSharedFlow<ProfileViewState>()
    val badgeSharedFlow: SharedFlow<ProfileViewState> =
        _badgeSharedFlow.asSharedFlow()

    fun doRequestBadge(request : BadgeRequest) {
        viewModelScope.launch {
            profileRepository.doRequestBadge(request)
                .onStart {
                    _badgeSharedFlow.emit(ProfileViewState.Loading)
                }
                .catch { exception ->
//                    CommonLogger.instance.sysLogE(
//                        "222 BadgeViewModel",
//                        exception.localizedMessage,
//                        exception
//                    )
                    onError(exception)
                }
                .collect {
                    _badgeSharedFlow.emit(
                        ProfileViewState.SuccessBadgeRequest(
                            message = it.msg.orEmpty(), badgeResponse = it)
                    )
                }
        }
    }

    private suspend fun onError(exception: Throwable) {
        when (exception) {
            is IOException,
            is TimeoutException
            -> {
                _badgeSharedFlow.emit(
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
                    _badgeSharedFlow.emit(ProfileViewState.InputError(errorResponse.errors))
                } else {
                    _badgeSharedFlow.emit(
                        ProfileViewState.PopupError(
                            PopupErrorState.HttpError, errorResponse?.msg.orEmpty()
                        )
                    )
                }
            }
            else -> _badgeSharedFlow.emit(
                ProfileViewState.PopupError(
                    PopupErrorState.UnknownError
                )
            )
        }
    }
}