package com.ziacare.app.data.repositories.wallet

import com.ziacare.app.data.repositories.baseresponse.GeneralResponse
import com.ziacare.app.data.repositories.wallet.request.GetGroupBalanceRequest
import com.ziacare.app.data.repositories.wallet.request.GroupSendPointsRequest
import com.ziacare.app.data.repositories.wallet.request.ScanQRRequest
import com.ziacare.app.data.repositories.wallet.request.SearchUserRequest
import com.ziacare.app.data.repositories.wallet.request.SendPointsToUserRequest
import com.ziacare.app.data.repositories.wallet.request.TopupRequest
import com.ziacare.app.data.repositories.wallet.request.TransactionDetailsRequest
import com.ziacare.app.data.repositories.wallet.request.TransactionListRequest
import com.ziacare.app.data.repositories.wallet.response.GetBalanceResponse
import com.ziacare.app.data.repositories.wallet.response.ScanQRResponse
import com.ziacare.app.data.repositories.wallet.response.SearchGroupResponse
import com.ziacare.app.data.repositories.wallet.response.SearchUserResponse
import com.ziacare.app.data.repositories.wallet.response.TransactionDetailsResponse
import com.ziacare.app.data.repositories.wallet.response.TransactionListResponse
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