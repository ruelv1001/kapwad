package com.lionscare.app.data.repositories.billing

import com.lionscare.app.data.repositories.billing.response.BillListResponse
import retrofit2.Response
import retrofit2.http.POST

interface BillService {

    @POST("api/bills/bulletin/all")
    suspend fun doGetAllBillList(): Response<BillListResponse>

}