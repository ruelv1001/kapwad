package com.lionscare.app.data.repositories.assistance

import com.lionscare.app.data.repositories.assistance.request.AssistanceRequest
import com.lionscare.app.data.repositories.baseresponse.GeneralResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AssistanceService {

    @POST("api/group/assistance/pending/all")
    suspend fun doGetAllAssistanceRequestList(@Body assistanceRequest: AssistanceRequest): Response<GeneralResponse>

    @POST("api/group/assistance/info")
    suspend fun doGetAssistanceRequestInfo(@Body assistanceRequest: AssistanceRequest): Response<GeneralResponse>

    @POST("api/group/assistance/pending/user")
    suspend fun doGetMyAssistanceRequestList(@Body assistanceRequest: AssistanceRequest): Response<GeneralResponse>

    @POST("api/group/assistance/pending/approve")
    suspend fun doApproveAssistanceRequest(@Body assistanceRequest: AssistanceRequest): Response<GeneralResponse>

    @POST("api/group/assistance/pending/decline")
    suspend fun doDeclineAssistanceRequest(@Body assistanceRequest: AssistanceRequest): Response<GeneralResponse>

    @POST("api/group/assistance/pending/cancel")
    suspend fun doCancelAssistanceRequest(@Body assistanceRequest: AssistanceRequest): Response<GeneralResponse>

}