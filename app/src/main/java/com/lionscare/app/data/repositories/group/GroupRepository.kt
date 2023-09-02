package com.lionscare.app.data.repositories.group

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.lionscare.app.data.repositories.group.request.CreateGroupRequest
import com.lionscare.app.data.repositories.group.request.GetGroupListRequest
import com.lionscare.app.data.repositories.group.response.CreateGroupResponse
import com.lionscare.app.data.repositories.group.response.GetGroupListResponse
import com.lionscare.app.data.repositories.group.response.GroupListData
import com.lionscare.app.data.repositories.group.response.ImmediateFamilyResponse
import com.lionscare.app.data.repositories.group.response.PendingGroupRequestData
import com.lionscare.app.security.AuthEncryptedDataManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
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

    fun doGetPendingGroupRequestList(pagingConfig: PagingConfig = getDefaultPageConfig()): Flow<PagingData<PendingGroupRequestData>>{
        val getPendingGroupRequestPagingSource = GetPendingGroupRequestPagingSource(groupRemoteDataSource)
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = { getPendingGroupRequestPagingSource }
        ).flow
            .flowOn(ioDispatcher)
    }

    private fun getDefaultPageConfig(): PagingConfig {
        return PagingConfig(pageSize = 20, initialLoadSize = 5, enablePlaceholders = false)
    }
}