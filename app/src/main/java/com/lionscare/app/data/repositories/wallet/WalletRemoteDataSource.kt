package com.lionscare.app.data.repositories.wallet

import com.lionscare.app.data.repositories.baseresponse.GeneralResponse
import com.lionscare.app.data.repositories.wallet.request.ScanQRRequest
import com.lionscare.app.data.repositories.wallet.request.SearchUserRequest
import com.lionscare.app.data.repositories.wallet.request.SendPointsToUserRequest
import com.lionscare.app.data.repositories.wallet.request.TopupRequest
import com.lionscare.app.data.repositories.wallet.request.TransactionDetailsRequest
import com.lionscare.app.data.repositories.wallet.request.TransactionListRequest
import com.lionscare.app.data.repositories.wallet.response.GetBalanceResponse
import com.lionscare.app.data.repositories.wallet.response.ScanQRResponse
import com.lionscare.app.data.repositories.wallet.response.SearchUserResponse
import com.lionscare.app.data.repositories.wallet.response.TransactionDetailsResponse
import com.lionscare.app.data.repositories.wallet.response.TransactionListResponse
import retrofit2.HttpException
import java.net.HttpURLConnection
import javax.inject.Inject

class WalletRemoteDataSource @Inject constructor(private val walletService: WalletService)  {

    suspend fun getWalletBalance(): GetBalanceResponse {
        val response = walletService.getWalletBalance()
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun getWalletTransaction(page: String, perPage: String): TransactionListResponse{
        val request = TransactionListRequest(per_page = perPage, page = page)
        val response = walletService.getWalletTransactions(request)

        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun getTransactionDetails(transactionId: String): TransactionDetailsResponse {
        val request = TransactionDetailsRequest(transactionId)
        val response = walletService.getTransactionsDetails(request)
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun doTopupPoints(amount: String): GeneralResponse {
        val request = TopupRequest(amount)
        val response = walletService.doTopupPoints(request)
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun doScanQR(userId: String): ScanQRResponse {
        val request = ScanQRRequest(userId)
        val response = walletService.doScanQr(request)
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun doSendPoints(userId: String, groupId: String, amount: String, notes: String): TransactionDetailsResponse {
        val request = SendPointsToUserRequest(
            amount = amount,
            user_id = userId,
            group_id = groupId,
            notes = notes
        )
        val response = walletService.doSendPoints(request)
        if (response.code() != HttpURLConnection.HTTP_CREATED) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun doSearchUser(keyword: String): SearchUserResponse{
        val request = SearchUserRequest(keyword)
        val response = walletService.doSearchUser(request)

        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

}