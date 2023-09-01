package com.lionscare.app.data.repositories.admin

import com.lionscare.app.data.repositories.member.request.ListOfMembersRequest
import com.lionscare.app.data.repositories.member.response.ListOfMembersResponse
import retrofit2.HttpException
import java.net.HttpURLConnection
import javax.inject.Inject

class AdminRemoteDataSource @Inject constructor(private val adminService: AdminService) {

    suspend fun doGetListOfAdmins(groupId: String? = null, page: String?= null): ListOfMembersResponse {
        val request = ListOfMembersRequest(group_id = groupId, page = page)
        val response = adminService.doGetListOfAdmin(request)
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }
    
}