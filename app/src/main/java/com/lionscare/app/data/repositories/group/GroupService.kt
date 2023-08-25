package com.lionscare.app.data.repositories.group

import com.lionscare.app.data.repositories.group.request.CreateGroupRequest
import com.lionscare.app.data.repositories.group.response.CreateGroupResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface GroupService {

    @POST("api/group/create")
    suspend fun doCreateGroup(@Body createGroupRequest: CreateGroupRequest): Response<CreateGroupResponse>

    @POST("api/group/update")
    suspend fun doUpdateGroup(@Body createGroupRequest: CreateGroupRequest): Response<CreateGroupResponse>

    @POST("api/group/show")
    suspend fun doShowGroup(@Body createGroupRequest: CreateGroupRequest): Response<CreateGroupResponse>
}