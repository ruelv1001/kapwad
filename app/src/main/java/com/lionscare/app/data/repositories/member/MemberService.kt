package com.lionscare.app.data.repositories.member

import com.lionscare.app.data.repositories.baseresponse.GeneralResponse
import com.lionscare.app.data.repositories.member.request.ListOfMembersRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface MemberService {

    @POST("api/group/member/all")
    suspend fun doGetListOfMembers(@Body listOfMembersRequest: ListOfMembersRequest): Response<GeneralResponse>
}