package da.farmer.app.data.repositories.wallet

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import da.farmer.app.data.repositories.baseresponse.GeneralResponse
import da.farmer.app.data.repositories.group.request.CreateGroupRequest
import da.farmer.app.data.repositories.group.response.CreateGroupResponse
import da.farmer.app.data.repositories.wallet.response.GetBalanceResponse
import da.farmer.app.data.repositories.wallet.response.QRData
import da.farmer.app.data.repositories.wallet.response.ScanQRResponse
import da.farmer.app.data.repositories.wallet.response.SearchGroupResponse
import da.farmer.app.data.repositories.wallet.response.SearchUserResponse
import da.farmer.app.data.repositories.wallet.response.TransactionData
import da.farmer.app.data.repositories.wallet.response.TransactionDetailsResponse
import da.farmer.app.security.AuthEncryptedDataManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GroupWalletRepository @Inject constructor(
    private val walletRemoteDataSource: GroupWalletRemoteDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
)   {

    fun getWalletBalance(groupId: String): Flow<GetBalanceResponse> {
        return flow {
            val response = walletRemoteDataSource.getWalletBalance(groupId)
            emit(response)
        }.flowOn(ioDispatcher)
    }


    fun doSendPoints(
        amount: String,
        groupId: String,
        receiverUserId: String,
        receiverGroupId: String,
        assistanceId : String ?= null
    ): Flow<TransactionDetailsResponse> {
        return flow {
            val response = walletRemoteDataSource.doSendPoints(
                amount,
                groupId,
                receiverUserId,
                receiverGroupId,
                assistanceId
            )
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun getTransactionList(pagingConfig: PagingConfig = getDefaultPageConfig(), groupId: String): Flow<PagingData<TransactionData>> {
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = { GroupWalletPagingSource(walletRemoteDataSource, groupId) }
        ).flow
            .flowOn(ioDispatcher)
    }

    private fun getDefaultPageConfig(): PagingConfig {
        return PagingConfig(pageSize = 10,enablePlaceholders = false)
    }
}