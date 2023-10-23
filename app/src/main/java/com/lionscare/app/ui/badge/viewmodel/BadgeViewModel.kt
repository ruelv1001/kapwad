package com.lionscare.app.ui.badge.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lionscare.app.data.model.ErrorModel
import com.lionscare.app.data.repositories.baseresponse.UserModel
import com.lionscare.app.data.repositories.profile.ProfileRepository
import com.lionscare.app.data.repositories.profile.request.BadgeRemovalRequest
import com.lionscare.app.data.repositories.profile.request.BadgeRequest
import com.lionscare.app.security.AuthEncryptedDataManager
import com.lionscare.app.ui.profile.viewmodel.ProfileViewState
import com.lionscare.app.utils.AppConstant
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
    private val profileRepository: ProfileRepository,
    val encryptedDataManager: AuthEncryptedDataManager
) : ViewModel() {
    private val _badgeSharedFlow = MutableSharedFlow<ProfileViewState>()
    val badgeSharedFlow: SharedFlow<ProfileViewState> =
        _badgeSharedFlow.asSharedFlow()


    //make it an observable to notify changes, making it a STATE
    private val _isBadgeRemovalRequestCancelled = MutableLiveData<Boolean>()
    val isBadgeRemovalRequestCancelled : LiveData<Boolean?> = _isBadgeRemovalRequestCancelled

    fun getUserKYC() : String{
        return encryptedDataManager.getKYCStatus()
    }
    fun setBadgeRemovalRequestCancelled(value: Boolean) {
        _isBadgeRemovalRequestCancelled.value = value
    }

    fun doRequestBadge(request : BadgeRequest) {
        viewModelScope.launch {
            profileRepository.doRequestBadge(request)
                .onStart {
                    _badgeSharedFlow.emit(ProfileViewState.Loading)
                }
                .catch { exception ->
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

    fun getBadgeStatus() {
        viewModelScope.launch {
            profileRepository.getBadgeStatus()
                .onStart {
                    _badgeSharedFlow.emit(ProfileViewState.LoadingBadgeStatus)
                }
                .catch { exception ->
                    onError(exception, "badge")
                }
                .collect {
                    _badgeSharedFlow.emit(
                        ProfileViewState.SuccessGetBadgeStatus(
                            message = it.msg.orEmpty(), badgeStatusResponse = it)
                    )
                }
        }
    }


    fun requestBadgeRemoval(request : BadgeRemovalRequest) {
        viewModelScope.launch {
            profileRepository.requestBadgeRemoval(request)
                .onStart {
                    _badgeSharedFlow.emit(ProfileViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _badgeSharedFlow.emit(
                        ProfileViewState.SuccessRequestBadgeRemoval(
                            message = it.msg.orEmpty())
                    )
                }
        }
    }

    fun getBadgeRemovalStatus() {
        viewModelScope.launch {
            profileRepository.getBadgeRemovalStatus()
                .onStart {
                    _badgeSharedFlow.emit(ProfileViewState.Loading)
                }
                .catch { exception ->
                    onError(exception, "badge_removal")
                }
                .collect {
                    _badgeSharedFlow.emit(
                        ProfileViewState.SuccessBadgeRemovalStatus(
                        badgeRemovalStatus = it.data)
                    )
                }
        }
    }

    fun cancelRequestBadgeRemoval() {
        viewModelScope.launch {
            profileRepository.cancelRequestBadgeRemoval()
                .onStart {
                    _badgeSharedFlow.emit(ProfileViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _badgeSharedFlow.emit(
                        ProfileViewState.Success(
                            message = it.msg.orEmpty())
                    )
                }
        }
    }
    private suspend fun onError(exception: Throwable, badge: String ="") {
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
                val errorResponse: ErrorModel? = gson.fromJson(errorBody?.charStream(), type)
                if (errorResponse?.has_requirements == true) {
                    _badgeSharedFlow.emit(ProfileViewState.InputError(errorResponse.errors))
                } else {
                    if(AppConstant.isSessionStatusCode(errorResponse?.status_code.orEmpty())){
                        _badgeSharedFlow.emit(
                            ProfileViewState.PopupError(
                                PopupErrorState.SessionError,
                                errorResponse?.msg.orEmpty()
                            )
                        )
                    }else if (errorResponse?.status_code.orEmpty() != AppConstant.NOT_FOUND){
                        _badgeSharedFlow.emit(
                            ProfileViewState.PopupError(
                                PopupErrorState.HttpError,
                                errorResponse?.msg.orEmpty(),
                            )
                        )
                    }else if (errorResponse?.status_code.orEmpty() == AppConstant.NOT_FOUND){
                        _badgeSharedFlow.emit(
                            ProfileViewState.PopupError(
                                PopupErrorState.HttpError,
                                errorResponse?.msg.orEmpty(),
                                badge
                            )
                        )
                    }

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