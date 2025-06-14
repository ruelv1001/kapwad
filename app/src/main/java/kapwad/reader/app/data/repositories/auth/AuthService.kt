package kapwad.reader.app.data.repositories.auth

import kapwad.reader.app.data.repositories.auth.request.DeleteOrDeactivateRequest
import kapwad.reader.app.data.repositories.baseresponse.GeneralResponse
import kapwad.reader.app.data.repositories.auth.request.LoginRequest
import kapwad.reader.app.data.repositories.auth.request.ValidateEmailRequest
import kapwad.reader.app.data.repositories.auth.response.LoginResponse
import kapwad.reader.app.data.repositories.auth.response.ReasonsResponse
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

    @POST("api/profile/setting/deactivate")
    suspend fun doDeleteOrDeactivate(@Body request: DeleteOrDeactivateRequest): Response<GeneralResponse>

    @POST("api/profile/setting/otp")
    suspend fun doDeleteOrDeactivateOTP(@Body request: DeleteOrDeactivateRequest): Response<GeneralResponse>

    @POST("api/setting/lov/reason")
    suspend fun doGetDeleteReason(): Response<ReasonsResponse>
}