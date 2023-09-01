package com.lionscare.app.data.repositories.member

import com.lionscare.app.data.repositories.baseresponse.GeneralResponse
import com.lionscare.app.data.repositories.member.request.AcceptDeclineRequest
import com.lionscare.app.data.repositories.member.request.LeaveGroupRequest
import com.lionscare.app.data.repositories.member.request.ListOfMembersRequest
import com.lionscare.app.data.repositories.member.response.PendingMemberResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface MemberService {

    @POST("api/group/member/all")
    suspend fun doGetListOfMembers(@Body listOfMembersRequest: ListOfMembersRequest): Response<GeneralResponse>

    @POST("api/group/member/leave")
    suspend fun doLeaveGroup(@Body leaveGroupRequest: LeaveGroupRequest): Response<GeneralResponse>

    @POST("api/group/member/join")
    suspend fun doJoinGroup(@Body listOfMembersRequest: ListOfMembersRequest): Response<GeneralResponse>


    //invite member folder
    @POST("api/group/member/invite")
    suspend fun doInvitationByOwner(@Body leaveGroupRequest: LeaveGroupRequest): Response<GeneralResponse>

    @POST("api/group/member/accept")
    suspend fun doAcceptInvitation(@Body acceptDeclineRequest: AcceptDeclineRequest): Response<GeneralResponse>


    //Pending member folder
    @POST("api/group/member/pending/all")
    suspend fun doGetAllPendingInviteAndRequest(@Body listOfMembersRequest: ListOfMembersRequest): Response<PendingMemberResponse>

    @POST("api/group/member/pending/request")
    suspend fun doGetAllPendingRequest(@Body listOfMembersRequest: ListOfMembersRequest): Response<PendingMemberResponse>

    @POST("api/group/member/pending/invitation")
    suspend fun doGetAllPendingInvitation(@Body listOfMembersRequest: ListOfMembersRequest): Response<PendingMemberResponse>
}