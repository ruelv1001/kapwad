package com.lionscare.app.data.repositories.admin

import com.lionscare.app.data.repositories.admin.request.AdminRequest
import com.lionscare.app.data.repositories.baseresponse.GeneralResponse
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

    suspend fun doRemoveMember(groupId: String, memberId: Int): GeneralResponse {
        val request = AdminRequest(group_id = groupId, member_id = memberId)
        val response = adminService.doRemoveMember(request)
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun doDemoteAdmin(groupId: String, memberId: Int): GeneralResponse {
        val request = AdminRequest(group_id = groupId, member_id = memberId)
        val response = adminService.doDemoteAdmin(request)
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun doPromoteMember(groupId: String, memberId: Int): GeneralResponse {
        val request = AdminRequest(group_id = groupId, member_id = memberId)
        val response = adminService.doPromoteMember(request)
        if (response.code() != HttpURLConnection.HTTP_CREATED) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }
    
}