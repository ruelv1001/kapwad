package com.ziacare.app.data.repositories.registration

import com.ziacare.app.data.repositories.auth.response.LoginResponse
import com.ziacare.app.data.repositories.baseresponse.GeneralResponse
import com.ziacare.app.data.repositories.baseresponse.UserModel
import com.ziacare.app.data.repositories.registration.request.OTPRequest
import com.ziacare.app.data.repositories.registration.request.RegistrationRequest
import com.ziacare.app.data.repositories.registration.response.OnboardingScanQRResponse
import com.ziacare.app.security.AuthEncryptedDataManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class RegistrationRepository @Inject constructor(
    private val registerRemoteDataSource: RegistrationRemoteDataSource,
    private val encryptedDataManager: AuthEncryptedDataManager,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    fun doValidateFields(registrationRequest: RegistrationRequest): Flow<GeneralResponse> {
        return flow {
            val response = registerRemoteDataSource.doValidateFields(registrationRequest)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun doRequestOTP(otpRequest: OTPRequest): Flow<GeneralResponse> {
        return flow {
            val response = registerRemoteDataSource.doRequestOTP(otpRequest)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun doRegister(request: RegistrationRequest): Flow<LoginResponse> {
        return flow {
            val response = registerRemoteDataSource.doRegisterAccount(request)
            val token = response.token.orEmpty()
            val userInfo = response.data ?: UserModel()
            encryptedDataManager.setAccessToken(token)
            encryptedDataManager.setUserBasicInfo(userInfo)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun doScanQR(code: String): Flow<OnboardingScanQRResponse> {
        return flow {
            val response = registerRemoteDataSource.doScanQR(code)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun doPrevalidatePassword(
        password: String,
        passwordConfirmation: String
    ): Flow<GeneralResponse> {
        return flow {
            val response = registerRemoteDataSource.doPrevalidatePassword(
                password = password,
                passwordConfirmation = passwordConfirmation
            )
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun doRequestOTP(code: String): Flow<GeneralResponse> {
        return flow {
            val response = registerRemoteDataSource.doRequestOTP(code)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun doValidateAndSetPassword(
        code: String,
        password: String,
        passwordConfirmation: String,
        otp: String
    ): Flow<LoginResponse> {
        return flow {
            val response = registerRemoteDataSource.doValidateAndSetPassword(
                code = code,
                password = password,
                passwordConfirmation = passwordConfirmation,
                otp = otp
            )
            val token = response.token.orEmpty()
            val userInfo = response.data ?: UserModel()
            encryptedDataManager.setAccessToken(token)
            encryptedDataManager.setUserBasicInfo(userInfo)
            emit(response)
        }.flowOn(ioDispatcher)
    }
}
