package dswd.ziacare.app.data.repositories.wallet

import dswd.ziacare.app.data.repositories.baseresponse.GeneralResponse
import dswd.ziacare.app.data.repositories.wallet.request.GetGroupBalanceRequest
import dswd.ziacare.app.data.repositories.wallet.request.GroupSendPointsRequest
import dswd.ziacare.app.data.repositories.wallet.request.ScanQRRequest
import dswd.ziacare.app.data.repositories.wallet.request.SearchUserRequest
import dswd.ziacare.app.data.repositories.wallet.request.SendPointsToUserRequest
import dswd.ziacare.app.data.repositories.wallet.request.TopupRequest
import dswd.ziacare.app.data.repositories.wallet.request.TransactionDetailsRequest
import dswd.ziacare.app.data.repositories.wallet.request.TransactionListRequest
import dswd.ziacare.app.data.repositories.wallet.response.GetBalanceResponse
import dswd.ziacare.app.data.repositories.wallet.response.ScanQRResponse
import dswd.ziacare.app.data.repositories.wallet.response.SearchGroupResponse
import dswd.ziacare.app.data.repositories.wallet.response.SearchUserResponse
import dswd.ziacare.app.data.repositories.wallet.response.TransactionDetailsResponse
import dswd.ziacare.app.data.repositories.wallet.response.TransactionListResponse
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