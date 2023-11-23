package com.ziacare.app.data.repositories.auth

import com.ziacare.app.data.repositories.baseresponse.GeneralResponse
import com.ziacare.app.data.repositories.auth.request.LoginRequest
import com.ziacare.app.data.repositories.auth.request.ValidateEmailRequest
import com.ziacare.app.data.repositories.auth.response.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("api/auth/login")
    suspend fun doLogin(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/auth/refresh-token")
    suspend fun doRefreshToken(): Response<LoginResponse>

    @POST("api/auth/logout")
    suspend fun doLogout(): Response<GeneralResponse>

    @POST("api/auth/reset-password")
    suspend fun doForgotPass(@Body request: ValidateEmailRequest): Response<GeneralResponse>
}