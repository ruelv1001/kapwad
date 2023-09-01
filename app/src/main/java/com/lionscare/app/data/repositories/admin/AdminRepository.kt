package com.lionscare.app.data.repositories.admin

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.lionscare.app.data.repositories.member.response.MemberListData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
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
        return PagingConfig(pageSize = 10, initialLoadSize = 5, enablePlaceholders = false)
    }
}