package com.lionscare.app.data.repositories.billing

import com.lionscare.app.data.repositories.billing.response.BillListResponse
import com.lionscare.app.data.repositories.group.request.GetGroupListRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface BillService {

    @POST("api/bills/bulletin/all")
    suspend fun doGetAllBillList(@Body getGroupListRequest: GetGroupListRequest): Response<BillListResponse>

    @POST("api/bills/bulletin/requests")
    suspend fun doGetAskForDonationList(@Body getGroupListRequest: GetGroupListRequest): Response<BillListResponse>


}