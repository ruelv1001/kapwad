package com.lionscare.app.data.repositories.group

import com.lionscare.app.data.repositories.group.request.CreateGroupRequest
import com.lionscare.app.data.repositories.group.response.CreateGroupResponse
import com.lionscare.app.security.AuthEncryptedDataManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GroupRepository @Inject constructor(
    private val groupRemoteDataSource: GroupRemoteDataSource,
    private val encryptedDataManager: AuthEncryptedDataManager,
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
}