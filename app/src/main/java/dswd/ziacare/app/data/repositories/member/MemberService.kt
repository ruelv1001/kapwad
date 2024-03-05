package dswd.ziacare.app.data.repositories.member

import dswd.ziacare.app.data.repositories.baseresponse.GeneralResponse
import dswd.ziacare.app.data.repositories.member.request.AcceptDeclineRequest
import dswd.ziacare.app.data.repositories.member.request.LeaveGroupRequest
import dswd.ziacare.app.data.repositories.member.request.ListOfMembersRequest
import dswd.ziacare.app.data.repositories.member.response.ApproveRequestResponse
import dswd.ziacare.app.data.repositories.member.response.JoinGroupResponse
import dswd.ziacare.app.data.repositories.member.response.ListOfMembersResponse
import dswd.ziacare.app.data.repositories.member.response.PendingMemberResponse
import dswd.ziacare.app.data.repositories.wallet.request.SearchUserRequest
import dswd.ziacare.app.data.repositories.wallet.response.SearchGroupResponse
import dswd.ziacare.app.data.repositories.wallet.response.SearchUserResponse
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