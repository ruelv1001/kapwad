package com.lionscare.app.data.repositories.assistance

import com.lionscare.app.data.repositories.assistance.request.AllAssistanceListRequest
import com.lionscare.app.data.repositories.assistance.request.AssistanceRequest
import com.lionscare.app.data.repositories.assistance.request.CreateAssistanceRequest
import com.lionscare.app.data.repositories.assistance.response.CreateAssistanceResponse
import com.lionscare.app.data.repositories.assistance.response.GetAllAssistanceRequestResponse
import com.lionscare.app.data.repositories.baseresponse.GeneralResponse
import retrofit2.HttpException
import java.net.HttpURLConnection
import javax.inject.Inject

class AssistanceRemoteDataSource @Inject constructor(private val assistanceService: AssistanceService) {

    suspend fun doCreateAssistance(request: CreateAssistanceRequest): GeneralResponse {
        val response = assistanceService.doCreateAssistance(request)
        if (response.code() != HttpURLConnection.HTTP_CREATED) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }
    suspend fun doGetAllAssistanceRequestList(
        groupId: String,
        per_page: Int,
        page : Int,
        filter: List<String>
    ): GetAllAssistanceRequestResponse {
        val request = AllAssistanceListRequest(group_id = groupId, per_page = per_page, page = page , filter = filter)
        val response = assistanceService.doGetAllAssistanceRequestList(request)
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun doGetAssistanceRequestInfo(
        referenceId: String? = null,
        groupId: String? = null
    ): CreateAssistanceResponse {
        val request = AssistanceRequest(group_id = groupId, reference_id = referenceId)
        val response = assistanceService.doGetAssistanceRequestInfo(request)
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun doGetMyAssistanceRequestList(
        groupId: String,
        per_page: Int,
        page : Int,
        filter: List<String>
    ): GetAllAssistanceRequestResponse {
        val request = AllAssistanceListRequest(group_id = groupId, per_page = per_page, page = page , filter = filter)
        val response = assistanceService.doGetMyAssistanceRequestList(request)
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun doApproveAssistanceRequest(
        requestId: String? = null,
        groupId: String? = null,
        remarks: String? = null
    ): GeneralResponse {
        val request = AssistanceRequest(request_id = requestId, group_id = groupId, remarks = remarks)
        val response = assistanceService.doApproveAssistanceRequest(request)
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun doDeclineAssistanceRequest(
        requestId: String? = null,
        groupId: String? = null,
        remarks: String? = null
    ): GeneralResponse {
        val request = AssistanceRequest(request_id = requestId, group_id = groupId, remarks = remarks)
        val response = assistanceService.doDeclineAssistanceRequest(request)
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun doCancelAssistanceRequest(
        requestId: String? = null,
        groupId: String? = null
    ): GeneralResponse {
        val request = AssistanceRequest(request_id = requestId, group_id = groupId)
        val response = assistanceService.doCancelAssistanceRequest(request)
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

}