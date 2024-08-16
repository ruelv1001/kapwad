package da.farmer.app.data.repositories.registration

import da.farmer.app.data.repositories.auth.response.LoginResponse
import da.farmer.app.data.repositories.baseresponse.GeneralResponse
import da.farmer.app.data.repositories.registration.request.OTPRequest
import da.farmer.app.data.repositories.registration.request.OnboardingRequest
import da.farmer.app.data.repositories.registration.request.RegistrationRequest
import da.farmer.app.data.repositories.registration.response.OnboardingScanQRResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RegistrationService {

    @POST("api/auth/pre-register")
    suspend fun doValidateFields(@Body registrationRequest: RegistrationRequest): Response<GeneralResponse>

    @POST("api/auth/request-otp")
    suspend fun doRequestOTP(@Body otpRequest: OTPRequest): Response<GeneralResponse>

    @POST("api/auth/register")
    suspend fun doRegisterAccount(@Body registrationRequest: RegistrationRequest): Response<LoginResponse>

    @POST("api/auth/onboarding/scan")
    suspend fun doScanQR(@Body onboardingRequest: OnboardingRequest): Response<OnboardingScanQRResponse>

    @POST("api/auth/onboarding/pre-validate")
    suspend fun doPrevalidatePassword(@Body onboardingRequest: OnboardingRequest): Response<GeneralResponse>

    @POST("api/auth/onboarding/request-otp")
    suspend fun doRequestOTP(@Body onboardingRequest: OnboardingRequest): Response<GeneralResponse>

    @POST("api/auth/onboarding/validate-otp")
    suspend fun doValidateAndSetPassword(@Body onboardingRequest: OnboardingRequest): Response<LoginResponse>
}