package com.lionscare.app.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lionscare.app.data.model.ErrorModel
import com.lionscare.app.data.repositories.group.GroupRepository
import com.lionscare.app.data.repositories.member.MemberRepository
import com.lionscare.app.security.AuthEncryptedDataManager
import com.lionscare.app.ui.group.viewmodel.MemberViewState
import com.lionscare.app.ui.wallet.viewmodel.WalletViewState
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
import java.io.IOException
import java.util.concurrent.TimeoutException
import javax.inject.Inject

@HiltViewModel
class GroupListViewModel @Inject constructor(
    private val groupRepository: GroupRepository,
    private val memberRepository: MemberRepository,
    private val encryptedDataManager: AuthEncryptedDataManager
) :
    ViewModel() {

    var getSelfGroupRequestCount : Int?= 0

    fun doGetSelfGroupRequestCount() : Int? {
        return getSelfGroupRequestCount
    }
    private val _getGroupSharedFlow = MutableSharedFlow<GroupListViewState>()

    val getGroupSharedFlow: SharedFlow<GroupListViewState> =
        _getGroupSharedFlow.asSharedFlow()


    fun getUserKYC() : String {
        return encryptedDataManager.getKYCStatus()
    }

    private suspend fun loadGroups() {
        groupRepository.doGetGroupList()
            .cachedIn(viewModelScope)
            .onStart {
                _getGroupSharedFlow.emit(GroupListViewState.Loading)
            }
            .catch { exception ->
                onError(exception)
                CommonLogger.devLog("error",exception)
            }
            .collect { pagingData ->
                _getGroupSharedFlow.emit(
                    GroupListViewState.Success(pagingData)
                )
            }
    }

    fun refreshAll() {
        viewModelScope.launch {
            loadGroups()
        }
    }

    private suspend fun loadPendingRequestList() {
        groupRepository.doGetPendingGroupRequestList()
            .cachedIn(viewModelScope)
            .onStart {
                _getGroupSharedFlow.emit(GroupListViewState.Loading)
            }
            .catch { exception ->
                onError(exception)
                CommonLogger.devLog("error",exception)
            }
            .collect { pagingData ->
                _getGroupSharedFlow.emit(
                    GroupListViewState.SuccessGetPendingRequestList(pagingData)
                )
            }
    }


    fun doAcceptInvitation(pendingMemberId: Long, groupId: String) {
        viewModelScope.launch {
            memberRepository.doAcceptInvitation(pendingMemberId, groupId)
                .onStart {

                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _getGroupSharedFlow.emit(
                        GroupListViewState.SuccessAcceptDeclineInvitation(it.msg.orEmpty())
                    )
                }
        }
    }

    fun doDeclineInvitation(pendingMemberId: Long, groupId: String) {
        viewModelScope.launch {
            memberRepository.doDeclineInvitation(pendingMemberId, groupId)
                .onStart {

                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _getGroupSharedFlow.emit(
                        GroupListViewState.SuccessAcceptDeclineInvitation(it.msg.orEmpty())
                    )
                }
        }
    }

    fun cancelJoinRequest(pending_id: String, group_id: String) {
        viewModelScope.launch {
            memberRepository.doCancelJoinRequest(pending_id, group_id)
                .onStart {

                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _getGroupSharedFlow.emit(
                        GroupListViewState.SuccessAcceptDeclineInvitation(it.msg.toString())
                    )
                }
        }
    }

    fun refreshPendingRequestList() {
        viewModelScope.launch {
            loadPendingRequestList()
        }
    }

    private suspend fun onError(exception: Throwable) {
        when (exception) {
            is IOException,
            is TimeoutException,
            -> {
                _getGroupSharedFlow.emit(
                    GroupListViewState.PopupError(
                        PopupErrorState.NetworkError
                    )
                )
            }
            is HttpException -> {
                val errorBody = exception.response()?.errorBody()
                val gson = Gson()
                val type = object : TypeToken<ErrorModel>() {}.type
                var errorResponse: ErrorModel? = gson.fromJson(errorBody?.charStream(), type)
                _getGroupSharedFlow.emit(
                    GroupListViewState.PopupError(
                        if (AppConstant.isSessionStatusCode(errorResponse?.status_code.orEmpty())){
                            PopupErrorState.SessionError
                        }else{
                            PopupErrorState.HttpError
                        }
                        , errorResponse?.msg.orEmpty()
                    )
                )
            }
            else -> _getGroupSharedFlow.emit(
                GroupListViewState.PopupError(
                    PopupErrorState.UnknownError
                )
            )
        }
    }
}