package com.lionscare.app.data.repositories.member

import com.lionscare.app.data.repositories.baseresponse.GeneralResponse
import com.lionscare.app.data.repositories.member.request.AcceptDeclineRequest
import com.lionscare.app.data.repositories.member.request.LeaveGroupRequest
import com.lionscare.app.data.repositories.member.request.ListOfMembersRequest
import com.lionscare.app.data.repositories.member.response.ListOfMembersResponse
import com.lionscare.app.data.repositories.member.response.PendingMemberResponse
import retrofit2.HttpException
import java.net.HttpURLConnection
import javax.inject.Inject

class MemberRemoteDataSource @Inject constructor(private val memberService: MemberService) {

    suspend fun doGetListOfMembers(groupId: String? = null, page: String?= null): ListOfMembersResponse {
        val request = ListOfMembersRequest(group_id = groupId, page = page)
        val response = memberService.doGetListOfMembers(request)
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

    suspend fun doGetAllPendingInviteAndRequest(groupId: String? = null, page: String?= null): PendingMemberResponse {
        val request = ListOfMembersRequest(group_id = groupId, page = page)
        val response = memberService.doGetAllPendingInviteAndRequest(request)
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }
    suspend fun doGetAllPendingRequest(groupId: String? = null, page: String?= null): PendingMemberResponse {
        val request = ListOfMembersRequest(group_id = groupId, page = page)
        val response = memberService.doGetAllPendingRequest(request)
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun doGetAllPendingInvitation(groupId: String? = null, page: String?= null): PendingMemberResponse {
        val request = ListOfMembersRequest(group_id = groupId, page = page)
        val response = memberService.doGetAllPendingInvitation(request)
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }
}