package com.lionscare.app.data.repositories.member

import com.lionscare.app.data.repositories.baseresponse.GeneralResponse
import com.lionscare.app.data.repositories.member.request.AcceptDeclineRequest
import com.lionscare.app.data.repositories.member.request.LeaveGroupRequest
import com.lionscare.app.data.repositories.member.request.ListOfMembersRequest
import com.lionscare.app.data.repositories.member.response.ApproveRequestResponse
import com.lionscare.app.data.repositories.member.response.JoinGroupResponse
import com.lionscare.app.data.repositories.member.response.ListOfMembersResponse
import com.lionscare.app.data.repositories.member.response.PendingMemberResponse
import com.lionscare.app.data.repositories.wallet.request.SearchUserRequest
import com.lionscare.app.data.repositories.wallet.response.SearchUserResponse
import retrofit2.HttpException
import java.net.HttpURLConnection
import javax.inject.Inject

class MemberRemoteDataSource @Inject constructor(private val memberService: MemberService) {

    suspend fun doGetListOfMembers(
        groupId: String? = null,
        page: String? = null
    ): ListOfMembersResponse {
        val request = ListOfMembersRequest(group_id = groupId, page = page)
        val response = memberService.doGetListOfMembers(request)
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun doLeaveGroup(group_id: String): GeneralResponse {
        val request = LeaveGroupRequest(group_id = group_id)
        val response = memberService.doLeaveGroup(request)
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun doJoinGroup(groupId: String, passcode: String? = null): JoinGroupResponse {
        val request = ListOfMembersRequest(group_id = groupId, passcode = passcode)
        val response = memberService.doJoinGroup(request)
        if (response.code() != HttpURLConnection.HTTP_CREATED) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun doInvitationByOwner(userId: String ,groupId: String): JoinGroupResponse {
        val request = LeaveGroupRequest(user_id = userId, group_id = groupId)
        val response = memberService.doInvitationByOwner(request)
        if (response.code() != HttpURLConnection.HTTP_CREATED) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun doAcceptInvitation(pending_id: String, group_id: String): JoinGroupResponse {
        val request = AcceptDeclineRequest(pending_id = pending_id, group_id = group_id)
        val response = memberService.doAcceptInvitation(request)
        if (response.code() != HttpURLConnection.HTTP_CREATED) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun doDeclineInvitation(pending_id: String, group_id: String): JoinGroupResponse {
        val request = AcceptDeclineRequest(pending_id = pending_id, group_id = group_id)
        val response = memberService.doDeclineInvitation(request)
        if (response.code() != HttpURLConnection.HTTP_CREATED) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun doGetAllPendingInviteAndRequest(
        groupId: String? = null,
        page: String? = null,
        type: String? = null
    ): PendingMemberResponse {
        val request = ListOfMembersRequest(group_id = groupId, page = page, type = type)
        val response = memberService.doGetAllPendingInviteAndRequest(request)
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun doApproveJoinRequest(pending_id: String, group_id: String): ApproveRequestResponse {
        val request = ListOfMembersRequest(pending_id = pending_id, group_id = group_id)
        val response = memberService.doApproveJoinRequest(request)
        if (response.code() != HttpURLConnection.HTTP_CREATED) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun doRejectJoinRequest(pending_id: String, group_id: String): GeneralResponse {
        val request = ListOfMembersRequest(pending_id = pending_id, group_id = group_id)
        val response = memberService.doRejectJoinRequest(request)
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun doSearchUser(keyword: String): SearchUserResponse {
        val request = SearchUserRequest(keyword)
        val response = memberService.doSearchUser(request)

        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }
}