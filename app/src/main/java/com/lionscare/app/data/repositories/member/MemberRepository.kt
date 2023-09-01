package com.lionscare.app.data.repositories.member

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.lionscare.app.data.repositories.baseresponse.GeneralResponse
import com.lionscare.app.data.repositories.group.response.GroupListData
import com.lionscare.app.data.repositories.member.request.AcceptDeclineRequest
import com.lionscare.app.data.repositories.member.request.LeaveGroupRequest
import com.lionscare.app.data.repositories.member.request.ListOfMembersRequest
import com.lionscare.app.data.repositories.member.response.MemberListData
import com.lionscare.app.data.repositories.member.response.PendingMemberData
import com.lionscare.app.data.repositories.member.response.PendingMemberResponse
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

    fun doLeaveGroup(leaveGroupRequest: LeaveGroupRequest): Flow<GeneralResponse> {
        return flow {
            val response = memberRemoteDataSource.doLeaveGroup(leaveGroupRequest)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun doJoinGroup(listOfMembersRequest: ListOfMembersRequest): Flow<GeneralResponse> {
        return flow {
            val response = memberRemoteDataSource.doJoinGroup(listOfMembersRequest)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun doInvitationByOwner(leaveGroupRequest: LeaveGroupRequest): Flow<GeneralResponse> {
        return flow {
            val response = memberRemoteDataSource.doInvitationByOwner(leaveGroupRequest)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun doAcceptInvitation(acceptDeclineRequest: AcceptDeclineRequest): Flow<GeneralResponse> {
        return flow {
            val response = memberRemoteDataSource.doAcceptInvitation(acceptDeclineRequest)
            emit(response)
        }.flowOn(ioDispatcher)
    }


    fun doGetAllPendingRequest(pagingConfig: PagingConfig = getDefaultPageConfig(), groupId: String): Flow<PagingData<PendingMemberData>> {
        val getAllPendingRequestPagingSource = GetAllPendingRequestPagingSource(memberRemoteDataSource, groupId)
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = { getAllPendingRequestPagingSource }
        ).flow
            .flowOn(ioDispatcher)
    }

    private fun getDefaultPageConfig(): PagingConfig {
        return PagingConfig(pageSize = 10, initialLoadSize = 5, enablePlaceholders = false)
    }
}