package com.lionscare.app.data.repositories.billing

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.lionscare.app.data.repositories.billing.response.BillData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
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