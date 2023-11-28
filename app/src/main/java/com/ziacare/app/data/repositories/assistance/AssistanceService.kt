package com.ziacare.app.data.repositories.assistance

import com.ziacare.app.data.repositories.assistance.request.AllAssistanceListRequest
import com.ziacare.app.data.repositories.assistance.request.AssistanceRequest
import com.ziacare.app.data.repositories.assistance.request.CreateAssistanceRequest
import com.ziacare.app.data.repositories.assistance.response.CreateAssistanceResponse
import com.ziacare.app.data.repositories.assistance.response.GetAllAssistanceRequestResponse
import com.ziacare.app.data.repositories.baseresponse.GeneralResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AssistanceService {

    @POST("api/group/assistance/create")
    suspend fun doCreateAssistance(@Body createAssistanceRequest: CreateAssistanceRequest): Response<GeneralResponse>
    @POST("api/group/assistance/all")
    suspend fun doGetAllAssistanceRequestList(@Body assistanceRequest: AllAssistanceListRequest): Response<GetAllAssistanceRequestResponse>

    @POST("api/group/assistance/info")
    suspend fun doGetAssistanceRequestInfo(@Body assistanceRequest: AssistanceRequest): Response<CreateAssistanceResponse>

    @POST("api/group/assistance/owned")
    suspend fun doGetMyAssistanceRequestList(@Body assistanceRequest: AllAssistanceListRequest): Response<GetAllAssistanceRequestResponse>

    @POST("api/group/assistance/pending/approve")
    suspend fun doApproveAssistanceRequest(@Body assistanceRequest: AssistanceRequest): Response<GeneralResponse>

    @POST("api/group/assistance/pending/decline")
    suspend fun doDeclineAssistanceRequest(@Body assistanceRequest: AssistanceRequest): Response<GeneralResponse>

    @POST("api/group/assistance/pending/cancel")
    suspend fun doCancelAssistanceRequest(@Body assistanceRequest: AssistanceRequest): Response<GeneralResponse>

}