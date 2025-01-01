package kapwad.reader.app.data.repositories.wallet

import kapwad.reader.app.data.repositories.baseresponse.GeneralResponse
import kapwad.reader.app.data.repositories.wallet.request.Scan2PayRequest
import kapwad.reader.app.data.repositories.wallet.request.ScanQRRequest
import kapwad.reader.app.data.repositories.wallet.request.SearchUserRequest
import kapwad.reader.app.data.repositories.wallet.request.SendPointsToUserRequest
import kapwad.reader.app.data.repositories.wallet.request.TopupRequest
import kapwad.reader.app.data.repositories.wallet.request.TransactionDetailsRequest
import kapwad.reader.app.data.repositories.wallet.request.TransactionListRequest
import kapwad.reader.app.data.repositories.wallet.response.GetBalanceResponse
import kapwad.reader.app.data.repositories.wallet.response.ScanQRResponse
import kapwad.reader.app.data.repositories.wallet.response.SearchGroupResponse
import kapwad.reader.app.data.repositories.wallet.response.SearchUserResponse
import kapwad.reader.app.data.repositories.wallet.response.TransactionDetailsResponse
import kapwad.reader.app.data.repositories.wallet.response.TransactionListResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface WalletService {

    @POST("api/wallet/user/history")
    suspend fun getWalletTransactions(@Body request: TransactionListRequest): Response<TransactionListResponse>

    @POST("api/wallet/user/show")
    suspend fun getTransactionsDetails(@Body request: TransactionDetailsRequest): Response<TransactionDetailsResponse>

    @POST("api/wallet/user/available-balance")
    suspend fun getWalletBalance(): Response<GetBalanceResponse>

    @POST("api/wallet/user/purchase")
    suspend fun doTopupPoints(@Body request: TopupRequest): Response<GeneralResponse>

    @POST("api/wallet/user/send")
    suspend fun doSendPoints(@Body request: SendPointsToUserRequest): Response<TransactionDetailsResponse>

    @POST("api/wallet/user/scan")
    suspend fun doScanQr(@Body request: ScanQRRequest): Response<ScanQRResponse>

    @POST("api/wallet/user/search/user")
    suspend fun doSearchUser(@Body request: SearchUserRequest): Response<SearchUserResponse>

    @POST("api/wallet/user/search/group")
    suspend fun doSearchGroup(@Body request: SearchUserRequest): Response<SearchGroupResponse>

    @POST("api/wallet/user/pay")
    suspend fun doScan2Pay(@Body request: Scan2PayRequest): Response<GeneralResponse>
}