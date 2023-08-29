package com.lionscare.app.data.repositories.wallet

import com.lionscare.app.data.repositories.baseresponse.GeneralResponse
import com.lionscare.app.data.repositories.wallet.request.TopupRequest
import com.lionscare.app.data.repositories.wallet.request.TransactionDetailsRequest
import com.lionscare.app.data.repositories.wallet.request.TransactionListRequest
import com.lionscare.app.data.repositories.wallet.response.GetBalanceResponse
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
        val request = TransactionListRequest(perPage, page)
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

}