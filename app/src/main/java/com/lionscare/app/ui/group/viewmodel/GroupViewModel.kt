package com.lionscare.app.ui.group.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lionscare.app.data.model.ErrorModel
import com.lionscare.app.data.repositories.group.GroupRepository
import com.lionscare.app.data.repositories.group.request.CreateGroupRequest
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
class GroupViewModel @Inject constructor(
    private val groupRepository: GroupRepository
) : ViewModel() {

    private val _groupSharedFlow = MutableSharedFlow<GroupViewState>()

    val groupSharedFlow: SharedFlow<GroupViewState> =
        _groupSharedFlow.asSharedFlow()

    fun createGroup(createGroupRequest: CreateGroupRequest) {
        viewModelScope.launch {
            groupRepository.doCreateGroup(createGroupRequest)
                .onStart {
                    _groupSharedFlow.emit(GroupViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
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

    fun showGroup(group_id: Int){
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
                            PopupErrorState.HttpError, errorResponse?.msg.orEmpty()
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