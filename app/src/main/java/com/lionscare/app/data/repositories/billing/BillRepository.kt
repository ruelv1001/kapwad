package com.lionscare.app.data.repositories.billing

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.lionscare.app.data.repositories.assistance.GetAllListOfAssistanceRequestPagingSource
import com.lionscare.app.data.repositories.assistance.request.CreateAssistanceRequest
import com.lionscare.app.data.repositories.baseresponse.GeneralResponse
import com.lionscare.app.data.repositories.billing.request.AskDonationRequest
import com.lionscare.app.data.repositories.billing.response.BillData
import com.lionscare.app.data.repositories.group.response.GroupListData
import com.lionscare.app.data.repositories.member.response.MemberListData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class BillRepository @Inject constructor(
    private val billRemoteDataSource: BillRemoteDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
)   {

    fun doGetAllBillList(pagingConfig: PagingConfig = getDefaultPageConfig()): Flow<PagingData<BillData>>{
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = { AllBillListPagingSource(billRemoteDataSource) }
        ).flow
            .flowOn(ioDispatcher)
    }

    fun doGetAskForDonationList(pagingConfig: PagingConfig = getDefaultPageConfig()): Flow<PagingData<BillData>>{
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = { AskForDonationListPagingSource(billRemoteDataSource) }
        ).flow
            .flowOn(ioDispatcher)
    }

    //==================Ask for donations
    fun doGetAllListOfGroupRequestedForDonations(pagingConfig: PagingConfig = getDefaultPageConfig(), code : String): Flow<PagingData<GroupListData>>{
        val doGetAllListOfGroupRequestedForDonations =
            ListOfGroupRequestedForDonationPagingSource(billRemoteDataSource, code = code)
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = { doGetAllListOfGroupRequestedForDonations }
        ).flow
            .flowOn(ioDispatcher)
    }

    fun doGetAllListOfUserRequestedForDonations(pagingConfig: PagingConfig = getDefaultPageConfig(), code : String): Flow<PagingData<MemberListData>>{
        val doGetAllListOfUserRequestedForDonations =
            ListOfUserRequestedForDonationPagingSource(billRemoteDataSource, code = code)
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = { doGetAllListOfUserRequestedForDonations }
        ).flow
            .flowOn(ioDispatcher)
    }

    fun doRequestToUsers(request: AskDonationRequest): Flow<GeneralResponse> {
        return flow {
            val response =
                billRemoteDataSource.doRequestToUsers(request)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun doRequestToGroups(request: AskDonationRequest): Flow<GeneralResponse> {
        return flow {
            val response =
                billRemoteDataSource.doRequestToGroups(request)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    private fun getDefaultPageConfig(): PagingConfig {
        return PagingConfig(pageSize = 5, initialLoadSize = 5, enablePlaceholders = false)
    }
}