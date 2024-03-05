package dswd.ziacare.app.data.repositories.wallet

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dswd.ziacare.app.data.repositories.baseresponse.GeneralResponse
import dswd.ziacare.app.data.repositories.group.request.CreateGroupRequest
import dswd.ziacare.app.data.repositories.group.response.CreateGroupResponse
import dswd.ziacare.app.data.repositories.wallet.response.GetBalanceResponse
import dswd.ziacare.app.data.repositories.wallet.response.QRData
import dswd.ziacare.app.data.repositories.wallet.response.ScanQRResponse
import dswd.ziacare.app.data.repositories.wallet.response.SearchGroupResponse
import dswd.ziacare.app.data.repositories.wallet.response.SearchUserResponse
import dswd.ziacare.app.data.repositories.wallet.response.TransactionData
import dswd.ziacare.app.data.repositories.wallet.response.TransactionDetailsResponse
import dswd.ziacare.app.security.AuthEncryptedDataManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
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

    fun doScanQr(userId: String): Flow<ScanQRResponse> {
        return flow {
            val response = walletRemoteDataSource.doScanQR(userId)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun doSendPoints(userId: String, groupId: String, amount: String, notes: String): Flow<TransactionDetailsResponse> {
        return flow {
            val response = walletRemoteDataSource.doSendPoints(userId, groupId, amount, notes)
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

    fun doSearchUser(keyword: String, pagingConfig: PagingConfig = getDefaultPageConfig()): Flow<PagingData<QRData>> {
        val searchUserPagingSource = SearchUserPagingSource(walletRemoteDataSource, keyword)
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = { searchUserPagingSource }
        ).flow
            .flowOn(ioDispatcher)
    }

    fun doSearchUser(keyword: String): Flow<SearchUserResponse> {
        return flow {
            val response = walletRemoteDataSource.doSearchUser(keyword)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun doSearchGroup(keyword: String): Flow<SearchGroupResponse> {
        return flow {
            val response = walletRemoteDataSource.doSearchGroup(keyword)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun doScan2Pay(amount: String, mid: String, remarks: String = ""): Flow<GeneralResponse> {
        return flow {
            val response = walletRemoteDataSource.doScan2Pay(amount, mid, remarks)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    private fun getDefaultPageConfig(): PagingConfig {
        return PagingConfig(pageSize = 10,enablePlaceholders = false)
    }
}