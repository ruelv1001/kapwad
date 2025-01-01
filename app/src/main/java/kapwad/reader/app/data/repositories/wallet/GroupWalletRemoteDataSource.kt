package kapwad.reader.app.data.repositories.wallet

import kapwad.reader.app.data.repositories.wallet.request.GetGroupBalanceRequest
import kapwad.reader.app.data.repositories.wallet.request.GroupSendPointsRequest
import kapwad.reader.app.data.repositories.wallet.request.TransactionListRequest
import kapwad.reader.app.data.repositories.wallet.response.GetBalanceResponse
import kapwad.reader.app.data.repositories.wallet.response.TransactionDetailsResponse
import kapwad.reader.app.data.repositories.wallet.response.TransactionListResponse
import retrofit2.HttpException
import java.net.HttpURLConnection
import javax.inject.Inject

class GroupWalletRemoteDataSource @Inject constructor(private val walletService: GroupWalletService)  {

    suspend fun getWalletBalance(groupId: String): GetBalanceResponse {
        val request = GetGroupBalanceRequest(groupId)
        val response = walletService.getWalletBalance(request)
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun getWalletTransaction(page: String, perPage: String, groupId: String): TransactionListResponse{
        val request = TransactionListRequest(per_page = perPage, page = page, group_id = groupId)
        val response = walletService.getWalletTransactions(request)

        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }


    suspend fun doSendPoints(
        amount: String,
        groupId: String,
        receiverUserId: String,
        receiverGroupId: String,
        assistanceId: String ?= null
    ): TransactionDetailsResponse {
        val request = GroupSendPointsRequest(
            amount = amount,
            group_id = groupId,
            receiver_user_id = receiverUserId,
            receiver_group_id = receiverGroupId,
            assistance_id = assistanceId
        )
        val response = walletService.doSendPoints(request)
        if (response.code() != HttpURLConnection.HTTP_CREATED) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

}