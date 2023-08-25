package com.lionscare.app.data.repositories.group

import com.lionscare.app.data.repositories.group.request.CreateGroupRequest
import com.lionscare.app.data.repositories.group.response.CreateGroupResponse
import retrofit2.HttpException
import java.net.HttpURLConnection
import javax.inject.Inject

class GroupRemoteDataSource @Inject constructor(private val groupService: GroupService)  {

    suspend fun doCreateGroup(createGroupRequest: CreateGroupRequest): CreateGroupResponse {
        val response = groupService.doCreateGroup(createGroupRequest)
        if (response.code() != HttpURLConnection.HTTP_CREATED) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun doUpdateGroup(createGroupRequest: CreateGroupRequest): CreateGroupResponse {
        val response = groupService.doUpdateGroup(createGroupRequest)
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun doShowGroup(createGroupRequest: CreateGroupRequest): CreateGroupResponse {
        val response = groupService.doShowGroup(createGroupRequest)
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

}