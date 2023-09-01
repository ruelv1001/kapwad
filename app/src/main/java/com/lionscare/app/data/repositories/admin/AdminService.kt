package com.lionscare.app.data.repositories.admin

import com.lionscare.app.data.repositories.member.request.ListOfMembersRequest
import com.lionscare.app.data.repositories.member.response.ListOfMembersResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AdminService {

    @POST("api/group/member/admin")
    suspend fun doGetListOfAdmin(@Body listOfMembersRequest: ListOfMembersRequest): Response<ListOfMembersResponse>

}