package dswd.ziacare.app.data.repositories.registration

import dswd.ziacare.app.data.repositories.auth.response.LoginResponse
import dswd.ziacare.app.data.repositories.baseresponse.GeneralResponse
import dswd.ziacare.app.data.repositories.registration.request.OTPRequest
import dswd.ziacare.app.data.repositories.registration.request.OnboardingRequest
import dswd.ziacare.app.data.repositories.registration.request.RegistrationRequest
import dswd.ziacare.app.data.repositories.registration.response.OnboardingScanQRResponse
import retrofit2.HttpException
import java.net.HttpURLConnection
import javax.inject.Inject

class RegistrationRemoteDataSource @Inject constructor(private val registrationService: RegistrationService) {

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

    suspend fun doRegisterAccount(registrationRequest: RegistrationRequest): LoginResponse {
        val response = registrationService.doRegisterAccount(registrationRequest)
        if (response.code() != HttpURLConnection.HTTP_CREATED) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun doScanQR(code: String): OnboardingScanQRResponse {
        val request = OnboardingRequest(code = code)
        val response = registrationService.doScanQR(request)
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun doPrevalidatePassword(
        password: String,
        passwordConfirmation: String
    ): GeneralResponse {
        val request =
            OnboardingRequest(password = password, password_confirmation = passwordConfirmation)
        val response = registrationService.doPrevalidatePassword(request)
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun doRequestOTP(code: String): GeneralResponse {
        val request = OnboardingRequest(code = code)
        val response = registrationService.doRequestOTP(request)
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun doValidateAndSetPassword(
        code: String,
        password: String,
        passwordConfirmation: String,
        otp: String
    ): LoginResponse {
        val request = OnboardingRequest(
            code = code,
            password = password,
            password_confirmation = passwordConfirmation,
            otp = otp
        )
        val response = registrationService.doValidateAndSetPassword(request)
        if (response.code() != HttpURLConnection.HTTP_CREATED) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

}