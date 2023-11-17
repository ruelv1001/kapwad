package com.lionscare.app.data.repositories.billing

import com.lionscare.app.data.repositories.billing.response.BillListResponse
import com.lionscare.app.data.repositories.group.request.GetGroupListRequest
import retrofit2.HttpException
import java.net.HttpURLConnection
import javax.inject.Inject

class BillRemoteDataSource @Inject constructor(private val billService: BillService)  {

    suspend fun doGetAllBillList(page: String, perPage: String): BillListResponse {
        val request = GetGroupListRequest(page.toInt(), perPage.toInt())
        val response = billService.doGetAllBillList(request)

        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

}