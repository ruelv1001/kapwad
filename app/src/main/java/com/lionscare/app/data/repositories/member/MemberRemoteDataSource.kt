package com.lionscare.app.data.repositories.member

import com.lionscare.app.data.repositories.baseresponse.GeneralResponse
import com.lionscare.app.data.repositories.member.request.AcceptDeclineRequest
import com.lionscare.app.data.repositories.member.request.LeaveGroupRequest
import com.lionscare.app.data.repositories.member.request.ListOfMembersRequest
import retrofit2.HttpException
import java.net.HttpURLConnection
import javax.inject.Inject

class MemberRemoteDataSource @Inject constructor(private val memberService: MemberService) {

    suspend fun doGetListOfMembers(listOfMembersRequest: ListOfMembersRequest): GeneralResponse {
        val response = memberService.doGetListOfMembers(listOfMembersRequest)
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun doLeaveGroup(leaveGroupRequest: LeaveGroupRequest): GeneralResponse {
        val response = memberService.doLeaveGroup(leaveGroupRequest)
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun doJoinGroup(listOfMembersRequest: ListOfMembersRequest): GeneralResponse {
        val response = memberService.doJoinGroup(listOfMembersRequest)
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun doInvitationByOwner(leaveGroupRequest: LeaveGroupRequest): GeneralResponse {
        val response = memberService.doInvitationByOwner(leaveGroupRequest)
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun doAcceptInvitation(acceptDeclineRequest: AcceptDeclineRequest): GeneralResponse {
        val response = memberService.doAcceptInvitation(acceptDeclineRequest)
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun doGetAllPendingMember(listOfMembersRequest: ListOfMembersRequest): GeneralResponse {
        val response = memberService.doGetAllPendingMember(listOfMembersRequest)
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }
}