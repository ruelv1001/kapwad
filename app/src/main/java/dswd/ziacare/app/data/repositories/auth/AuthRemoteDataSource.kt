package dswd.ziacare.app.data.repositories.auth

import dswd.ziacare.app.data.repositories.auth.request.DeleteOrDeactivateRequest
import dswd.ziacare.app.data.repositories.baseresponse.GeneralResponse
import dswd.ziacare.app.data.repositories.auth.request.LoginRequest
import dswd.ziacare.app.data.repositories.auth.request.ValidateEmailRequest
import dswd.ziacare.app.data.repositories.auth.response.LoginResponse
import dswd.ziacare.app.data.repositories.auth.response.ReasonsResponse
import retrofit2.HttpException
import java.net.HttpURLConnection
import javax.inject.Inject

class AuthRemoteDataSource @Inject constructor(private val authService: AuthService)  {

    suspend fun doLogin(email: String, password: String): LoginResponse{
        val request = LoginRequest(email, password)
        val response = authService.doLogin(request)

        //Check if response code is 200 else it will throw HttpException
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }

        //Will automatically throw a NullPointerException when response.body() is Null

        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun doRefreshToken(): LoginResponse{
        val response = authService.doRefreshToken()

        //Check if response code is 200 else it will throw HttpException
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }

        //Will automatically throw a NullPointerException when response.body() is Null

        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun doLogout(): GeneralResponse {
        val response = authService.doLogout()
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun doForgotPass(email: String): GeneralResponse{
        val request = ValidateEmailRequest(email = email)
        val response = authService.doForgotPass(request)
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun doDeleteOrDeactivateAccount(reasonId: String, other: String? =null, type: String): GeneralResponse{
        val request = DeleteOrDeactivateRequest(reason_id = reasonId, other_reason = other, type = type)
        val response = authService.doDeleteOrDeactivate(request)
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun doDeleteOrDeactivateAccountOTP(reasonId: String, other: String? =null, type: String, otp: String): GeneralResponse{
        val request = DeleteOrDeactivateRequest(reason_id = reasonId, other_reason = other, type = type, otp = otp)
        val response = authService.doDeleteOrDeactivateOTP(request)
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun getReasonsList(): ReasonsResponse {
        val response = authService.doGetDeleteReason()
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

}