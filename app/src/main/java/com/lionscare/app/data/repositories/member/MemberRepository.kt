package com.lionscare.app.data.repositories.member

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.lionscare.app.data.repositories.baseresponse.GeneralResponse
import com.lionscare.app.data.repositories.group.response.GroupListData
import com.lionscare.app.data.repositories.member.request.AcceptDeclineRequest
import com.lionscare.app.data.repositories.member.request.LeaveGroupRequest
import com.lionscare.app.data.repositories.member.request.ListOfMembersRequest
import com.lionscare.app.data.repositories.member.response.ApproveRequestResponse
import com.lionscare.app.data.repositories.member.response.JoinGroupResponse
import com.lionscare.app.data.repositories.member.response.MemberListData
import com.lionscare.app.data.repositories.member.response.PendingMemberData
import com.lionscare.app.data.repositories.member.response.PendingMemberResponse
import com.lionscare.app.data.repositories.wallet.response.SearchUserResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MemberRepository @Inject constructor(
    private val memberRemoteDataSource: MemberRemoteDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    fun doGetListOfMember(pagingConfig: PagingConfig = getDefaultPageConfig(), groupId: String): Flow<PagingData<MemberListData>> {
        val getListOfMembersPagingSource = GetListOfMembersPagingSource(memberRemoteDataSource, groupId)
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = { getListOfMembersPagingSource }
        ).flow
            .flowOn(ioDispatcher)
    }

    fun doLeaveGroup(group_id: String): Flow<GeneralResponse> {
        return flow {
            val response = memberRemoteDataSource.doLeaveGroup(group_id)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun doJoinGroup(groupId: String, passcode: String? = null): Flow<JoinGroupResponse> {
        return flow {
            val response = memberRemoteDataSource.doJoinGroup(groupId, passcode)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun doInvitationByOwner(userId: String ,groupId: String): Flow<JoinGroupResponse> {
        return flow {
            val response = memberRemoteDataSource.doInvitationByOwner(userId, groupId)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun doAcceptInvitation(pending_id: String, group_id: String): Flow<JoinGroupResponse> {
        return flow {
            val response = memberRemoteDataSource.doAcceptInvitation(pending_id, group_id)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun doDeclineInvitation(pending_id: String, group_id: String): Flow<JoinGroupResponse> {
        return flow {
            val response = memberRemoteDataSource.doDeclineInvitation(pending_id, group_id)
            emit(response)
        }.flowOn(ioDispatcher)
    }


    fun doGetAllPendingRequest(pagingConfig: PagingConfig = getDefaultPageConfig(), groupId: String, type: String? = null): Flow<PagingData<PendingMemberData>> {
        val getAllPendingRequestPagingSource = GetAllPendingRequestPagingSource(memberRemoteDataSource, groupId = groupId, type = type)
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = { getAllPendingRequestPagingSource }
        ).flow
            .flowOn(ioDispatcher)
    }

    private fun getDefaultPageConfig(): PagingConfig {
        return PagingConfig(pageSize = 10, initialLoadSize = 5, enablePlaceholders = false)
    }

    fun doApproveJoinRequest(pending_id: String, group_id: String): Flow<ApproveRequestResponse> {
        return flow {
            val response = memberRemoteDataSource.doApproveJoinRequest(pending_id, group_id)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun doRejectJoinRequest(pending_id: String, group_id: String): Flow<GeneralResponse> {
        return flow {
            val response = memberRemoteDataSource.doRejectJoinRequest(pending_id, group_id)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun doSearchUser(keyword: String): Flow<SearchUserResponse> {
        return flow {
            val response = memberRemoteDataSource.doSearchUser(keyword)
            emit(response)
        }.flowOn(ioDispatcher)
    }
}