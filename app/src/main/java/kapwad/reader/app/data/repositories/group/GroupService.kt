package kapwad.reader.app.data.repositories.group

import kapwad.reader.app.data.repositories.baseresponse.GeneralResponse
import kapwad.reader.app.data.repositories.group.request.CreateGroupRequest
import kapwad.reader.app.data.repositories.group.request.GetGroupListRequest
import kapwad.reader.app.data.repositories.group.response.CreateGroupResponse
import kapwad.reader.app.data.repositories.group.response.GetGroupListResponse
import kapwad.reader.app.data.repositories.group.response.ImmediateFamilyResponse
import kapwad.reader.app.data.repositories.group.response.PendingGroupRequestsListResponse
import kapwad.reader.app.data.repositories.wallet.request.SearchUserRequest
import kapwad.reader.app.data.repositories.wallet.response.SearchGroupResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface GroupService {

    @POST("api/group/create")
    suspend fun doCreateGroup(@Body createGroupRequest: CreateGroupRequest): Response<CreateGroupResponse>

    @POST("api/group/update")
    suspend fun doUpdateGroup(@Body createGroupRequest: CreateGroupRequest): Response<CreateGroupResponse>

    @POST("api/group/info")
    suspend fun doShowGroup(@Body createGroupRequest: CreateGroupRequest): Response<CreateGroupResponse>

    @POST("api/group/family")
    suspend fun doGetImmediateFamily(): Response<ImmediateFamilyResponse>
    @POST("api/group/all")
    suspend fun doGetGroupList(@Body getGroupListRequest: GetGroupListRequest): Response<GetGroupListResponse>

    @POST("api/group/pending")
    suspend fun doGetPendingGroupRequest(@Body getGroupListRequest: GetGroupListRequest): Response<PendingGroupRequestsListResponse>


    @POST("api/group/search")
    suspend fun doSearchGroup(@Body request: SearchUserRequest): Response<SearchGroupResponse>

    @Multipart
    @POST("api/group/update-avatar")
    suspend fun uploadGroupAvatar(
        @Part image: MultipartBody.Part,
        @Part group_id: MultipartBody.Part
    ): Response<GeneralResponse>
}