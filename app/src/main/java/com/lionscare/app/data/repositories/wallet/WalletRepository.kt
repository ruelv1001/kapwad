package com.lionscare.app.data.repositories.wallet

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.lionscare.app.data.repositories.baseresponse.GeneralResponse
import com.lionscare.app.data.repositories.group.request.CreateGroupRequest
import com.lionscare.app.data.repositories.group.response.CreateGroupResponse
import com.lionscare.app.data.repositories.wallet.response.GetBalanceResponse
import com.lionscare.app.data.repositories.wallet.response.TransactionData
import com.lionscare.app.data.repositories.wallet.response.TransactionDetailsResponse
import com.lionscare.app.security.AuthEncryptedDataManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class WalletRepository @Inject constructor(
    private val walletRemoteDataSource: WalletRemoteDataSource,
    private val walletPagingSource: WalletPagingSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
)   {

    fun getWalletBalance(): Flow<GetBalanceResponse> {
        return flow {
            val response = walletRemoteDataSource.getWalletBalance()
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun getTransactionDetails(transactionId: String): Flow<TransactionDetailsResponse> {
        return flow {
            val response = walletRemoteDataSource.getTransactionDetails(transactionId)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun doTopupPoints(amount: String): Flow<GeneralResponse> {
        return flow {
            val response = walletRemoteDataSource.doTopupPoints(amount)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun doScanQr(userId: String): Flow<GeneralResponse> {
        return flow {
            val response = walletRemoteDataSource.doScanQR(userId)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun doSendPoints(amount: String, userId: String): Flow<GeneralResponse> {
        return flow {
            val response = walletRemoteDataSource.doSendPoints(amount, userId)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun getTransactionList(pagingConfig: PagingConfig = getDefaultPageConfig()): Flow<PagingData<TransactionData>> {
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = { walletPagingSource }
        ).flow
            .flowOn(ioDispatcher)
    }

    private fun getDefaultPageConfig(): PagingConfig {
        return PagingConfig(pageSize = 10,enablePlaceholders = false)
    }
}