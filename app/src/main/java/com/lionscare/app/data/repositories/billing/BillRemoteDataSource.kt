package com.lionscare.app.data.repositories.billing

import com.lionscare.app.data.repositories.billing.response.BillListResponse
import retrofit2.HttpException
import java.net.HttpURLConnection
import javax.inject.Inject

class BillRemoteDataSource @Inject constructor(private val billService: BillService)  {

    suspend fun doGetAllBillList(): BillListResponse {
        val response = billService.doGetAllBillList()

        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

}