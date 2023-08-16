package com.lionscare.app.data.repositories.registration

import com.lionscare.app.data.repositories.GeneralResponse
import com.lionscare.app.data.repositories.registration.request.OTPRequest
import com.lionscare.app.data.repositories.registration.request.RegistrationRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RegistrationService {

    @POST("api/auth/pre-register")
    suspend fun doValidateFields(@Body registrationRequest: RegistrationRequest): Response<GeneralResponse>

    @POST("api/auth/request-otp")
    suspend fun doRequestOTP(@Body otpRequest: OTPRequest): Response<GeneralResponse>

    @POST("api/auth/register")
    suspend fun doRegisterAccount(@Body registrationRequest: RegistrationRequest): Response<GeneralResponse>
}