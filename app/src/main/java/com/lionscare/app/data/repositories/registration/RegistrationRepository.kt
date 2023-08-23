package com.lionscare.app.data.repositories.registration

import com.lionscare.app.data.repositories.auth.response.LoginResponse
import com.lionscare.app.data.repositories.baseresponse.GeneralResponse
import com.lionscare.app.data.repositories.baseresponse.UserModel
import com.lionscare.app.data.repositories.registration.request.OTPRequest
import com.lionscare.app.data.repositories.registration.request.RegistrationRequest
import com.lionscare.app.security.AuthEncryptedDataManager
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
)  {

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
            emit(response)
        }.flowOn(ioDispatcher)
    }

}