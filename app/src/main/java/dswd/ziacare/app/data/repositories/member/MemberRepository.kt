package dswd.ziacare.app.data.repositories.member

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dswd.ziacare.app.data.repositories.baseresponse.GeneralResponse
import dswd.ziacare.app.data.repositories.baseresponse.UserModel
import dswd.ziacare.app.data.repositories.group.response.GroupListData
import dswd.ziacare.app.data.repositories.member.request.AcceptDeclineRequest
import dswd.ziacare.app.data.repositories.member.request.LeaveGroupRequest
import dswd.ziacare.app.data.repositories.member.request.ListOfMembersRequest
import dswd.ziacare.app.data.repositories.member.response.ApproveRequestResponse
import dswd.ziacare.app.data.repositories.member.response.JoinGroupResponse
import dswd.ziacare.app.data.repositories.member.response.MemberListData
import dswd.ziacare.app.data.repositories.member.response.PendingMemberData
import dswd.ziacare.app.data.repositories.member.response.PendingMemberResponse
import dswd.ziacare.app.data.repositories.wallet.response.SearchUserResponse
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

    fun doGetListOfMember(pagingConfig: PagingConfig = getDefaultPageConfig(), groupId: String, include_admin: Boolean? = null, ownerInfo: UserModel? = null): Flow<PagingData<MemberListData>> {
        val getListOfMembersPagingSource = GetListOfMembersPagingSource(memberRemoteDataSource, groupId = groupId, include_admin = include_admin, ownerInfo = ownerInfo)
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

    fun doAcceptInvitation(pending_id: Long, group_id: String): Flow<JoinGroupResponse> {
        return flow {
            val response = memberRemoteDataSource.doAcceptInvitation(pending_id, group_id)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun doDeclineInvitation(pending_id: Long, group_id: String): Flow<JoinGroupResponse> {
        return flow {
            val response = memberRemoteDataSource.doDeclineInvitation(pending_id, group_id)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun doCancelInvitation(pending_id: String, group_id: String): Flow<GeneralResponse> {
        return flow {
            val response = memberRemoteDataSource.doCancelInvitation(pending_id, group_id)
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
        return PagingConfig(pageSize = 5, initialLoadSize = 5, enablePlaceholders = false)
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

    fun doCancelJoinRequest(pending_id: String, group_id: String): Flow<GeneralResponse> {
        return flow {
            val response = memberRemoteDataSource.doCancelJoinRequest(pending_id, group_id)
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