package com.lionscare.app.data.repositories.member

import com.lionscare.app.data.repositories.baseresponse.GeneralResponse
import com.lionscare.app.data.repositories.member.request.ListOfMembersRequest
import retrofit2.HttpException
import java.net.HttpURLConnection
import javax.inject.Inject

class MemberRemoteDataSource @Inject constructor(private val memberService: MemberService) {

    suspend fun doGetListOfMembers(listOfMembersRequest: ListOfMembersRequest): GeneralResponse {
        val response = memberService.doGetListOfMembers(listOfMembersRequest)
        if (response.code() != HttpURLConnection.HTTP_CREATED) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

}