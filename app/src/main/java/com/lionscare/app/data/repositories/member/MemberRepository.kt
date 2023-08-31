package com.lionscare.app.data.repositories.member

import com.lionscare.app.data.repositories.baseresponse.GeneralResponse
import com.lionscare.app.data.repositories.member.request.AcceptDeclineRequest
import com.lionscare.app.data.repositories.member.request.LeaveGroupRequest
import com.lionscare.app.data.repositories.member.request.ListOfMembersRequest
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

    fun doGetListOfMember(listOfMembersRequest: ListOfMembersRequest): Flow<GeneralResponse> {
        return flow {
            val response = memberRemoteDataSource.doGetListOfMembers(listOfMembersRequest)
            emit(response)
        }.flowOn(ioDispatcher)
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

    fun doGetAllPendingMember(listOfMembersRequest: ListOfMembersRequest): Flow<GeneralResponse> {
        return flow {
            val response = memberRemoteDataSource.doGetAllPendingMember(listOfMembersRequest)
            emit(response)
        }.flowOn(ioDispatcher)
    }
}