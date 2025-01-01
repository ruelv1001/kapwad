package kapwad.reader.app.data.repositories.wallet

import kapwad.reader.app.data.repositories.wallet.request.GetGroupBalanceRequest
import kapwad.reader.app.data.repositories.wallet.request.GroupSendPointsRequest
import kapwad.reader.app.data.repositories.wallet.request.TransactionListRequest
import kapwad.reader.app.data.repositories.wallet.response.GetBalanceResponse
import kapwad.reader.app.data.repositories.wallet.response.TransactionDetailsResponse
import kapwad.reader.app.data.repositories.wallet.response.TransactionListResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface GroupWalletService {

    @POST("api/wallet/group/history")
    suspend fun getWalletTransactions(@Body request: TransactionListRequest): Response<TransactionListResponse>

    @POST("api/wallet/group/available-balance")
    suspend fun getWalletBalance(@Body request: GetGroupBalanceRequest): Response<GetBalanceResponse>

    @POST("api/wallet/group/send")
    suspend fun doSendPoints(@Body request: GroupSendPointsRequest): Response<TransactionDetailsResponse>

}