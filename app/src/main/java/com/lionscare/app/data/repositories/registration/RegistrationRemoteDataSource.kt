package com.lionscare.app.data.repositories.registration

import com.lionscare.app.data.repositories.GeneralResponse
import com.lionscare.app.data.repositories.registration.request.OTPRequest
import com.lionscare.app.data.repositories.registration.request.RegistrationRequest
import retrofit2.HttpException
import java.net.HttpURLConnection
import javax.inject.Inject

class RegistrationRemoteDataSource @Inject constructor(private val registrationService: RegistrationService)  {

    suspend fun doValidateFields(registrationRequest: RegistrationRequest): GeneralResponse {
        val response = registrationService.doValidateFields(registrationRequest)
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun doRequestOTP(otpRequest: OTPRequest): GeneralResponse {
        val response = registrationService.doRequestOTP(otpRequest)
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun doRegisterAccount(registrationRequest: RegistrationRequest): GeneralResponse {
        val response = registrationService.doRegisterAccount(registrationRequest)
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

}