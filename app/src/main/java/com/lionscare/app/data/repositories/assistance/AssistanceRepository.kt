package com.lionscare.app.data.repositories.assistance

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.lionscare.app.data.repositories.admin.GetListOfAdminPagingSource
import com.lionscare.app.data.repositories.assistance.request.CreateAssistanceRequest
import com.lionscare.app.data.repositories.assistance.response.CreateAssistanceData
import com.lionscare.app.data.repositories.assistance.response.CreateAssistanceResponse
import com.lionscare.app.data.repositories.assistance.response.GetAllAssistanceRequestResponse
import com.lionscare.app.data.repositories.baseresponse.GeneralResponse
import com.lionscare.app.data.repositories.member.response.MemberListData
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

    fun doGetAllListOfAssistanceRequest(
        pagingConfig: PagingConfig = getDefaultPageConfig(),
        groupId: String,
        filter: List<String>
    ): Flow<PagingData<CreateAssistanceData>> {
        val getAllListOfAssistanceRequestPagingSource =
            GetAllListOfAssistanceRequestPagingSource(assistanceRemoteDataSource, groupId, filter)
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = { getAllListOfAssistanceRequestPagingSource }
        ).flow
            .flowOn(ioDispatcher)
    }

    fun doGetMyListOfAssistanceRequest(
        pagingConfig: PagingConfig = getDefaultPageConfig(),
        groupId: String,
        filter: List<String>
    ): Flow<PagingData<CreateAssistanceData>> {
        val getMyListOfAssistanceRequestPagingSource =
            GetMyListOfAssistanceRequestPagingSource(assistanceRemoteDataSource, groupId, filter)
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = { getMyListOfAssistanceRequestPagingSource }
        ).flow
            .flowOn(ioDispatcher)
    }

    fun doGetAssistanceRequestInfo(
        referenceId: String? = null,
        groupId: String? = null
    ): Flow<CreateAssistanceResponse> {
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