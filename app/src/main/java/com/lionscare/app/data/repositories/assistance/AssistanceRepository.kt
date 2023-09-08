package com.lionscare.app.data.repositories.assistance

import androidx.paging.PagingConfig
import com.lionscare.app.data.repositories.assistance.request.CreateAssistanceRequest
import com.lionscare.app.data.repositories.baseresponse.GeneralResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AssistanceRepository @Inject constructor(
    private val assistanceRemoteDataSource: AssistanceRemoteDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    fun doCreateAssistance(request: CreateAssistanceRequest): Flow<GeneralResponse> {
        return flow {
            val response =
                assistanceRemoteDataSource.doCreateAssistance(request)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun doGetAssistanceRequestInfo(
        referenceId: String? = null,
        groupId: String? = null
    ): Flow<GeneralResponse> {
        return flow {
            val response =
                assistanceRemoteDataSource.doGetAssistanceRequestInfo(referenceId, groupId)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun doApproveAssistanceRequest(
        requestId: String? = null,
        groupId: String? = null,
        remarks: String? = null
    ): Flow<GeneralResponse> {
        return flow {
            val response =
                assistanceRemoteDataSource.doApproveAssistanceRequest(requestId, groupId, remarks)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun doDeclineAssistanceRequest(
        requestId: String? = null,
        groupId: String? = null,
        remarks: String? = null
    ): Flow<GeneralResponse> {
        return flow {
            val response =
                assistanceRemoteDataSource.doDeclineAssistanceRequest(requestId, groupId, remarks)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun doCancelAssistanceRequest(
        requestId: String? = null,
        groupId: String? = null
    ): Flow<GeneralResponse> {
        return flow {
            val response =
                assistanceRemoteDataSource.doCancelAssistanceRequest(requestId, groupId)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    private fun getDefaultPageConfig(): PagingConfig {
        return PagingConfig(pageSize = 5, initialLoadSize = 5, enablePlaceholders = false)
    }

}