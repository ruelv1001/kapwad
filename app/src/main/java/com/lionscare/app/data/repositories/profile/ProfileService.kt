package com.lionscare.app.data.repositories.profile

import com.lionscare.app.data.repositories.auth.response.LoginResponse
import com.lionscare.app.data.repositories.profile.request.UpdateInfoRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ProfileService {
    @POST("api/profile/setting/info")
    suspend fun getProfileInfo(): Response<LoginResponse>

    @POST("api/profile/setting/update-info")
    suspend fun doUpdateInfo(@Body request: UpdateInfoRequest): Response<LoginResponse>

}