package com.ziacare.app.data.repositories.admin

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ziacare.app.data.repositories.admin.request.AdminRequest
import com.ziacare.app.data.repositories.auth.response.LoginResponse
import com.ziacare.app.data.repositories.baseresponse.GeneralResponse
import com.ziacare.app.data.repositories.baseresponse.UserModel
import com.ziacare.app.data.repositories.member.response.MemberListData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AdminRepository @Inject constructor(
    private val adminRemoteDataSource: AdminRemoteDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    fun doGetListOfAdmin(pagingConfig: PagingConfig = getDefaultPageConfig(), groupId: String): Flow<PagingData<MemberListData>> {
        val getListOfAdminPagingSource = GetListOfAdminPagingSource(adminRemoteDataSource, groupId)
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = { getListOfAdminPagingSource }
        ).flow
            .flowOn(ioDispatcher)
    }

    private fun getDefaultPageConfig(): PagingConfig {
        return PagingConfig(pageSize = 5, initialLoadSize = 5, enablePlaceholders = false)
    }

    fun doRemoveMember(groupId: String, memberId: Int) : Flow<GeneralResponse> {
        return flow{
            val response = adminRemoteDataSource.doRemoveMember(groupId,memberId)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun doDemoteAdmin(groupId: String, memberId: Int) : Flow<GeneralResponse> {
        return flow{
            val response = adminRemoteDataSource.doDemoteAdmin(groupId,memberId)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun doPromoteMember(groupId: String, memberId: Int) : Flow<GeneralResponse> {
        return flow{
            val response = adminRemoteDataSource.doPromoteMember(groupId,memberId)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun doTransferOwnership(groupId: String, userId: String) : Flow<GeneralResponse> {
        return flow{
            val response = adminRemoteDataSource.doTransferOwnership(groupId,userId)
            emit(response)
        }.flowOn(ioDispatcher)
    }
}