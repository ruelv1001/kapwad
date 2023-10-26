package com.lionscare.app.ui.group.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lionscare.app.data.model.ErrorModel
import com.lionscare.app.data.repositories.group.GroupRepository
import com.lionscare.app.data.repositories.group.request.CreateGroupRequest
import com.lionscare.app.data.repositories.profile.request.ProfileAvatarRequest
import com.lionscare.app.ui.main.viewmodel.GroupListViewState
import com.lionscare.app.ui.profile.viewmodel.ProfileViewState
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
class GroupViewModel @Inject constructor(
    private val groupRepository: GroupRepository
) : ViewModel() {

    var curretGroupCount = 0

    var avatarFileHolder : File? = null
    var avatarURIHolder : Uri? = null

    private val _groupSharedFlow = MutableSharedFlow<GroupViewState>()

    val groupSharedFlow: SharedFlow<GroupViewState> =
        _groupSharedFlow.asSharedFlow()


    fun doGetGroupListCount() {
        viewModelScope.launch {
            groupRepository.doGetPendingGroupListCount()
                .onStart {
                    _groupSharedFlow.emit(GroupViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _groupSharedFlow.emit(
                        GroupViewState.SuccessPendingGroupListCount(it)
                    )
                }
        }
    }


    fun createGroup(createGroupRequest: CreateGroupRequest) {
        viewModelScope.launch {
            groupRepository.doCreateGroup(createGroupRequest)
                .onStart {
                    _groupSharedFlow.emit(GroupViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                    CommonLogger.sysLogE("CREATE ERROR", exception)
                }
                .collect {
                    _groupSharedFlow.emit(
                        GroupViewState.SuccessCreateGroup(it)
                    )
                }
        }
    }

    fun updateGroup(createGroupRequest: CreateGroupRequest) {
        viewModelScope.launch {
            groupRepository.doUpdateGroup(createGroupRequest)
                .onStart {
                    _groupSharedFlow.emit(GroupViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _groupSharedFlow.emit(
                        GroupViewState.SuccessUpdateGroup(it)
                    )
                }
        }
    }

    fun showGroup(group_id: String){
        val createGroupRequest = CreateGroupRequest(group_id = group_id)
        viewModelScope.launch {
            groupRepository.doShowGroup(createGroupRequest)
                .onStart {
                    _groupSharedFlow.emit(GroupViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect{
                    _groupSharedFlow.emit(GroupViewState.SuccessShowGroup(it))
                }
        }
    }

    fun doSearchGroupWithLoading(keyword: String) {
        viewModelScope.launch {
            groupRepository.doSearchGroup(keyword)
                .onStart {
                    _groupSharedFlow.emit(GroupViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _groupSharedFlow.emit(
                        GroupViewState.SuccessSearchGroup(it.data.orEmpty())
                    )
                }
        }
    }

    fun uploadGroupAvatar(imageFile: File, groupId: String) {
        viewModelScope.launch {
            groupRepository.uploadGroupAvatar(imageFile, groupId)
                .onStart {
                    _groupSharedFlow.emit(GroupViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _groupSharedFlow.emit(
                        GroupViewState.SuccessUploadAvatar(it.msg.orEmpty())
                    )
                }
        }
    }

    private suspend fun onError(exception: Throwable) {
        when (exception) {
            is IOException,
            is TimeoutException,
            -> {
                _groupSharedFlow.emit(
                    GroupViewState.PopupError(
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
                    _groupSharedFlow.emit(GroupViewState.InputError(errorResponse.errors))
                } else {
                    _groupSharedFlow.emit(
                        GroupViewState.PopupError(
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

            else -> _groupSharedFlow.emit(
                GroupViewState.PopupError(
                    PopupErrorState.UnknownError
                )
            )
        }
    }
}