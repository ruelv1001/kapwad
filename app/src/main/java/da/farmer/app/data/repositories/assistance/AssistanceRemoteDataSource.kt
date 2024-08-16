package da.farmer.app.data.repositories.assistance

import da.farmer.app.data.repositories.assistance.request.AllAssistanceListRequest
import da.farmer.app.data.repositories.assistance.request.AssistanceRequest
import da.farmer.app.data.repositories.assistance.request.CreateAssistanceRequest
import da.farmer.app.data.repositories.assistance.response.CreateAssistanceResponse
import da.farmer.app.data.repositories.assistance.response.GetAllAssistanceRequestResponse
import da.farmer.app.data.repositories.baseresponse.GeneralResponse
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
        referenceId: String? = null,
        groupId: String? = null,
        remarks: String? = null
    ): GeneralResponse {
        val request = AssistanceRequest(reference_id = referenceId, group_id = groupId, remarks = remarks)
        val response = assistanceService.doApproveAssistanceRequest(request)
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun doDeclineAssistanceRequest(
        referenceId: String? = null,
        groupId: String? = null,
        remarks: String? = null
    ): GeneralResponse {
        val request = AssistanceRequest(reference_id = referenceId, group_id = groupId, remarks = remarks)
        val response = assistanceService.doDeclineAssistanceRequest(request)
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun doCancelAssistanceRequest(
        referenceId: String? = null,
        groupId: String? = null
    ): GeneralResponse {
        val request = AssistanceRequest(reference_id = referenceId, group_id = groupId)
        val response = assistanceService.doCancelAssistanceRequest(request)
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

}