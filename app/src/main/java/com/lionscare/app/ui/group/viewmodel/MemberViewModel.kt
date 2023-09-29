package com.lionscare.app.ui.group.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lionscare.app.data.model.ErrorModel
import com.lionscare.app.data.repositories.baseresponse.UserModel
import com.lionscare.app.data.repositories.member.MemberRepository
import com.lionscare.app.security.AuthEncryptedDataManager
import com.lionscare.app.ui.main.viewmodel.GroupListViewState
import com.lionscare.app.utils.AppConstant
import com.lionscare.app.utils.CommonLogger
import com.lionscare.app.utils.PopupErrorState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeoutException
import javax.inject.Inject

@HiltViewModel
class MemberViewModel @Inject constructor(
    private val memberRepository: MemberRepository,
    encryptedDataManager: AuthEncryptedDataManager
) : ViewModel() {

    val user = encryptedDataManager.getUserBasicInfo()
    private val _memberSharedFlow = MutableSharedFlow<MemberViewState>()

    var ownerInfo = UserModel()

    val memberSharedFlow: SharedFlow<MemberViewState> =
        _memberSharedFlow.asSharedFlow()

    private suspend fun loadPendingMemberRequest(groupId: String, type : String) {
        memberRepository.doGetAllPendingRequest(groupId = groupId, type = type)
            .cachedIn(viewModelScope)
            .onStart {
                _memberSharedFlow.emit(MemberViewState.Loading)
            }
            .catch { exception ->
                onError(exception)
                CommonLogger.devLog("error", exception)
            }
            .collectLatest { pagingData ->
                _memberSharedFlow.emit(
                    MemberViewState.SuccessGetPendingRequest(pagingData)
                )
            }
    }

    fun refresh(groupId: String, type : String) {
        viewModelScope.launch {
            loadPendingMemberRequest(groupId, type)
        }
    }

    private suspend fun loadListOfMembers(groupId: String, includeAdmin: Boolean? = null) {
        memberRepository.doGetListOfMember(
            groupId = groupId,
            include_admin = includeAdmin,
            ownerInfo = ownerInfo
        )
            .cachedIn(viewModelScope)
            .onStart {
                _memberSharedFlow.emit(MemberViewState.Loading)
            }
            .catch { exception ->
                onError(exception)
                CommonLogger.devLog("error", exception)
            }
            .collect { pagingData ->
                _memberSharedFlow.emit(
                    MemberViewState.SuccessGetListOfMembers(pagingData)
                )
            }
    }

    fun refreshListOfMembers(groupId: String, includeAdmin: Boolean? = null) {
        viewModelScope.launch {
            loadListOfMembers(groupId, includeAdmin)
        }
    }

    fun approveJoinRequest(pending_id: String, group_id: String) {
        viewModelScope.launch {
            memberRepository.doApproveJoinRequest(pending_id, group_id)
                .onStart {
                    _memberSharedFlow.emit(MemberViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _memberSharedFlow.emit(
                        MemberViewState.SuccessApproveJoinRequest(it)
                    )
                }
        }
    }

    fun rejectJoinRequest(pending_id: String, group_id: String) {
        viewModelScope.launch {
            memberRepository.doRejectJoinRequest(pending_id, group_id)
                .onStart {
                    _memberSharedFlow.emit(MemberViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _memberSharedFlow.emit(
                        MemberViewState.SuccessRejectJoinRequest(it.msg.toString())
                    )
                }
        }
    }

    fun joinGroup(group_id: String, passcode: String? = null){
        viewModelScope.launch {
            memberRepository.doJoinGroup(group_id, passcode)
                .onStart {
                    _memberSharedFlow.emit(MemberViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _memberSharedFlow.emit(
                        MemberViewState.SuccessJoinGroup(it)
                    )
                }
        }
    }

    fun leaveGroup(group_id: String) {
        viewModelScope.launch {
            memberRepository.doLeaveGroup(group_id)
                .onStart {
                    _memberSharedFlow.emit(MemberViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _memberSharedFlow.emit(
                        MemberViewState.SuccessLeaveGroup(it.msg.toString())
                    )
                }
        }
    }

    fun doSearchUser(keyword: String) {
        viewModelScope.launch {
            memberRepository.doSearchUser(keyword)
                .onStart {
                    _memberSharedFlow.emit(MemberViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _memberSharedFlow.emit(
                        MemberViewState.SuccessSearchUser(it.data.orEmpty())
                    )
                }
        }
    }

    fun inviteMember(userId: String ,group_id: String) {
        viewModelScope.launch {
            memberRepository.doInvitationByOwner(userId = userId, groupId = group_id)
                .onStart {
                    _memberSharedFlow.emit(MemberViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                    CommonLogger.sysLogE("ERROR", exception)
                }
                .collect {
                    _memberSharedFlow.emit(
                        MemberViewState.SuccessInviteMember(it)
                    )
                }
        }
    }

    fun cancelInvitation(pending_id: String, group_id: String) {
        viewModelScope.launch {
            memberRepository.doCancelInvitation(pending_id, group_id)
                .onStart {

                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _memberSharedFlow.emit(
                        MemberViewState.SuccessRejectJoinRequest(it.msg.toString())
                    )
                }
        }
    }

    private suspend fun onError(exception: Throwable) {
        when (exception) {
            is IOException,
            is TimeoutException,
            -> {
                _memberSharedFlow.emit(
                    MemberViewState.PopupError(
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
                    _memberSharedFlow.emit(MemberViewState.InputError(errorResponse.errors))
                } else {
                    _memberSharedFlow.emit(
                        MemberViewState.PopupError(
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

            else -> _memberSharedFlow.emit(
                MemberViewState.PopupError(
                    PopupErrorState.UnknownError
                )
            )
        }
    }
}