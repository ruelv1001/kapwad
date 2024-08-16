package da.farmer.app.data.repositories.wallet

import da.farmer.app.data.repositories.baseresponse.GeneralResponse
import da.farmer.app.data.repositories.wallet.request.GetGroupBalanceRequest
import da.farmer.app.data.repositories.wallet.request.GroupSendPointsRequest
import da.farmer.app.data.repositories.wallet.request.ScanQRRequest
import da.farmer.app.data.repositories.wallet.request.SearchUserRequest
import da.farmer.app.data.repositories.wallet.request.SendPointsToUserRequest
import da.farmer.app.data.repositories.wallet.request.TopupRequest
import da.farmer.app.data.repositories.wallet.request.TransactionDetailsRequest
import da.farmer.app.data.repositories.wallet.request.TransactionListRequest
import da.farmer.app.data.repositories.wallet.response.GetBalanceResponse
import da.farmer.app.data.repositories.wallet.response.ScanQRResponse
import da.farmer.app.data.repositories.wallet.response.SearchGroupResponse
import da.farmer.app.data.repositories.wallet.response.SearchUserResponse
import da.farmer.app.data.repositories.wallet.response.TransactionDetailsResponse
import da.farmer.app.data.repositories.wallet.response.TransactionListResponse
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