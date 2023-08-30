package com.lionscare.app.data.repositories.group

import com.lionscare.app.data.repositories.group.request.CreateGroupRequest
import com.lionscare.app.data.repositories.group.request.GetGroupListRequest
import com.lionscare.app.data.repositories.group.response.CreateGroupResponse
import com.lionscare.app.data.repositories.group.response.GetGroupListResponse
import com.lionscare.app.data.repositories.group.response.ImmediateFamilyResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

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
}