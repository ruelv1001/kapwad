package kapwad.reader.app.data.repositories.admin

import kapwad.reader.app.data.repositories.admin.request.AdminRequest
import kapwad.reader.app.data.repositories.baseresponse.GeneralResponse
import kapwad.reader.app.data.repositories.member.request.ListOfMembersRequest
import kapwad.reader.app.data.repositories.member.response.ListOfMembersResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AdminService {

    @POST("api/group/member/admin")
    suspend fun doGetListOfAdmin(@Body listOfMembersRequest: ListOfMembersRequest): Response<ListOfMembersResponse>

    @POST("api/group/member/remove?")
    suspend fun doRemoveMember(@Body request: AdminRequest): Response<GeneralResponse>

    @POST("api/group/member/demote")
    suspend fun doDemoteAdmin(@Body request: AdminRequest): Response<GeneralResponse>

    @POST("api/group/member/promote")
    suspend fun doPromoteMember(@Body request: AdminRequest): Response<GeneralResponse>

    @POST("api/group/member/transfer")
    suspend fun doTransferOwnership(@Body request: AdminRequest): Response<GeneralResponse>
}