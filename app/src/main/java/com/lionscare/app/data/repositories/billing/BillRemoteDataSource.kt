package com.lionscare.app.data.repositories.billing

import com.lionscare.app.data.repositories.baseresponse.GeneralResponse
import com.lionscare.app.data.repositories.billing.request.AskDonationRequest
import com.lionscare.app.data.repositories.billing.request.GetListOfAskedDonationRequest
import com.lionscare.app.data.repositories.billing.response.BillListResponse
import com.lionscare.app.data.repositories.group.request.GetGroupListRequest
import com.lionscare.app.data.repositories.group.response.GetGroupListResponse
import com.lionscare.app.data.repositories.member.response.ListOfMembersResponse
import retrofit2.HttpException
import java.net.HttpURLConnection
import javax.inject.Inject

class BillRemoteDataSource @Inject constructor(private val billService: BillService) {

    suspend fun doGetAllBillList(page: String, perPage: String): BillListResponse {
        val request = GetGroupListRequest(page.toInt(), perPage.toInt())
        val response = billService.doGetAllBillList(request)

        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun doGetAskForDonationList(page: String, perPage: String): BillListResponse {
        val request = GetGroupListRequest(page.toInt(), perPage.toInt())
        val response = billService.doGetAskForDonationList(request)

        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    //===================ask for donation
    suspend fun doGetAllListOfGroupRequestedForDonations(
        page: String,
        perPage: String,
        code: String
    ): GetGroupListResponse {
        val request = GetListOfAskedDonationRequest(
            page = page.toInt(),
            per_page = perPage.toInt(),
            code = code
        )
        val response = billService.doGetAllListOfGroupRequestedForDonations(request)

        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun doGetAllListOfUserRequestedForDonations(
        page: String,
        perPage: String,
        code: String
    ): ListOfMembersResponse {
        val request = GetListOfAskedDonationRequest(
            page = page.toInt(),
            per_page = perPage.toInt(),
            code = code
        )
        val response = billService.doGetAllListOfUserRequestedForDonations(request)

        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }
    suspend fun doRequestToUsers(request: AskDonationRequest): GeneralResponse {
        val response = billService.doRequestToUsers(request)
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun doRequestToGroups(request: AskDonationRequest): GeneralResponse {
        val response = billService.doRequestToGroups(request)
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

}