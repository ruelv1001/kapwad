package kapwad.reader.app.data.repositories.group

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kapwad.reader.app.data.repositories.baseresponse.GeneralResponse
import kapwad.reader.app.data.repositories.group.request.CreateGroupRequest
import kapwad.reader.app.data.repositories.group.response.CreateGroupResponse
import kapwad.reader.app.data.repositories.group.response.GroupListData
import kapwad.reader.app.data.repositories.group.response.ImmediateFamilyResponse
import kapwad.reader.app.data.repositories.group.response.PendingGroupRequestData
import kapwad.reader.app.data.repositories.group.response.PendingGroupRequestsListResponse
import kapwad.reader.app.data.repositories.wallet.response.SearchGroupResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import javax.inject.Inject

class GroupRepository @Inject constructor(
    private val groupRemoteDataSource: GroupRemoteDataSource,
    private val getGroupPagingSource: GetGroupPagingSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
)   {

    fun doCreateGroup(createGroupRequest: CreateGroupRequest): Flow<CreateGroupResponse> {
        return flow {
            val response = groupRemoteDataSource.doCreateGroup(createGroupRequest)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun doUpdateGroup(createGroupRequest: CreateGroupRequest): Flow<CreateGroupResponse> {
        return flow {
            val response = groupRemoteDataSource.doUpdateGroup(createGroupRequest)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun doShowGroup(createGroupRequest: CreateGroupRequest): Flow<CreateGroupResponse> {
        return flow {
            val response = groupRemoteDataSource.doShowGroup(createGroupRequest)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun doGetImmediateFamily(): Flow<ImmediateFamilyResponse> {
        return flow {
            val response = groupRemoteDataSource.doGetImmediateFamily()
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun doGetGroupList(pagingConfig: PagingConfig = getDefaultPageConfig()): Flow<PagingData<GroupListData>>{
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = { getGroupPagingSource }
        ).flow
            .flowOn(ioDispatcher)
    }
    fun doGetPendingGroupListCount(): Flow<PendingGroupRequestsListResponse>{
        return flow {
            val response = groupRemoteDataSource.doGetPendingGroupRequest(null, null)
            emit(response)
        }.flowOn(ioDispatcher)
    }


    fun doGetPendingGroupRequestList(pagingConfig: PagingConfig = getDefaultPageConfig()): Flow<PagingData<PendingGroupRequestData>>{
        val getPendingGroupRequestPagingSource = GetPendingGroupRequestPagingSource(groupRemoteDataSource)
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = { getPendingGroupRequestPagingSource }
        ).flow
            .flowOn(ioDispatcher)
    }

    fun doSearchGroup(keyword: String): Flow<SearchGroupResponse> {
        return flow {
            val response = groupRemoteDataSource.doSearchGroup(keyword)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun uploadGroupAvatar(imageFile: File, groupId: String): Flow<GeneralResponse> {
        return flow {
            val response = groupRemoteDataSource.uploadGroupAvatar(imageFile, groupId)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    private fun getDefaultPageConfig(): PagingConfig {
        return PagingConfig(pageSize = 5, initialLoadSize = 5, enablePlaceholders = false)
    }
}