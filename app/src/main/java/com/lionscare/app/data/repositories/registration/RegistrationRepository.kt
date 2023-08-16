package com.lionscare.app.data.repositories.registration

import com.lionscare.app.data.repositories.GeneralResponse
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

    fun doRegisterAccount(registrationRequest: RegistrationRequest): Flow<GeneralResponse> {
        return flow {
            val response = registerRemoteDataSource.doRegisterAccount(registrationRequest)
            /*val userInfo = response.data?: UserModel()
            encryptedDataManager.setUserBasicInfo(userInfo)
            val accessToken = response.token.orEmpty()
            encryptedDataManager.setAccessToken(accessToken)*/
            emit(response)
        }.flowOn(ioDispatcher)
    }

}