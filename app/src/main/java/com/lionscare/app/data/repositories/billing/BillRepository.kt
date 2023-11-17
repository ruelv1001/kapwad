package com.lionscare.app.data.repositories.billing

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.lionscare.app.data.repositories.baseresponse.GeneralResponse
import com.lionscare.app.data.repositories.billing.response.BillData
import com.lionscare.app.data.repositories.group.request.CreateGroupRequest
import com.lionscare.app.data.repositories.group.request.GetGroupListRequest
import com.lionscare.app.data.repositories.group.response.CreateGroupResponse
import com.lionscare.app.data.repositories.group.response.GetGroupListResponse
import com.lionscare.app.data.repositories.group.response.GroupListData
import com.lionscare.app.data.repositories.group.response.ImmediateFamilyResponse
import com.lionscare.app.data.repositories.group.response.PendingGroupRequestData
import com.lionscare.app.data.repositories.group.response.PendingGroupRequestsListResponse
import com.lionscare.app.data.repositories.profile.request.ProfileAvatarRequest
import com.lionscare.app.data.repositories.wallet.response.SearchGroupResponse
import com.lionscare.app.security.AuthEncryptedDataManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import javax.inject.Inject

class BillRepository @Inject constructor(
    private val billRemoteDataSource: BillRemoteDataSource,
    private val allBillListPagingSource: AllBillListPagingSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
)   {

    fun doGetAllBillList(pagingConfig: PagingConfig = getDefaultPageConfig()): Flow<PagingData<BillData>>{
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = { allBillListPagingSource }
        ).flow
            .flowOn(ioDispatcher)
    }

    private fun getDefaultPageConfig(): PagingConfig {
        return PagingConfig(pageSize = 5, initialLoadSize = 5, enablePlaceholders = false)
    }
}