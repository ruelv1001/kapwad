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
import com.lionscare.app.data.repositories.wallet.response.SearchGroupResponse
import com.lionscare.app.data.repositories.wallet.response.SearchUserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface MemberService {

    @POST("api/group/member/all")
    suspend fun doGetListOfMembers(@Body listOfMembersRequest: ListOfMembersRequest): Response<ListOfMembersResponse>

    @POST("api/group/member/leave")
    suspend fun doLeaveGroup(@Body leaveGroupRequest: LeaveGroupRequest): Response<GeneralResponse>

    @POST("api/group/member/join")
    suspend fun doJoinGroup(@Body listOfMembersRequest: ListOfMembersRequest): Response<JoinGroupResponse>


    //invite member folder
    @POST("api/group/member/invite")
    suspend fun doInvitationByOwner(@Body leaveGroupRequest: LeaveGroupRequest): Response<JoinGroupResponse>

    @POST("api/group/member/accept")
    suspend fun doAcceptInvitation(@Body acceptDeclineRequest: AcceptDeclineRequest): Response<JoinGroupResponse>
    @POST("api/group/member/decline")
    suspend fun doDeclineInvitation(@Body acceptDeclineRequest: AcceptDeclineRequest): Response<JoinGroupResponse>
    @POST("api/group/member/pending/cancel")
    suspend fun doCancelInvitation(@Body listOfMembersRequest: ListOfMembersRequest): Response<GeneralResponse>

    //Pending member folder
    @POST("api/group/member/pending/all")
    suspend fun doGetAllPendingInviteAndRequest(@Body listOfMembersRequest: ListOfMembersRequest): Response<PendingMemberResponse>

    @POST("api/group/member/pending/approve")
    suspend fun doApproveJoinRequest(@Body listOfMembersRequest: ListOfMembersRequest): Response<ApproveRequestResponse>
    @POST("api/group/member/pending/reject")
    suspend fun doRejectJoinRequest(@Body listOfMembersRequest: ListOfMembersRequest): Response<GeneralResponse>
    @POST("api/group/member/cancel")
    suspend fun doCancelJoinRequst(@Body listOfMembersRequest: ListOfMembersRequest): Response<GeneralResponse>

    @POST("api/group/member/search")
    suspend fun doSearchUser(@Body request: SearchUserRequest): Response<SearchUserResponse>
}