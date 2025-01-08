package kapwad.reader.app.data.repositories.auth

import kapwad.reader.app.data.local.UserLocalData
import kapwad.reader.app.data.repositories.baseresponse.GeneralResponse
import kapwad.reader.app.data.repositories.auth.response.LoginResponse
import kapwad.reader.app.data.repositories.auth.response.ReasonsResponse
import kapwad.reader.app.data.repositories.baseresponse.UserModel
import kapwad.reader.app.security.AuthEncryptedDataManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject



class AuthRepository @Inject constructor(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val encryptedDataManager: AuthEncryptedDataManager,
    private val authLocalDataSource: AuthLocalDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    fun doLogin(email : String, password : String) : Flow<LoginResponse> {
        return flow{
            val response = authRemoteDataSource.doLogin(email, password)
            val userInfo = response.data?: UserModel()
            val token = response.token.orEmpty()
            encryptedDataManager.setAccessToken(token)
           // encryptedDataManager.setUserBasicInfo(userInfo)
           // authLocalDataSource.login(setUpUserLocalData(userInfo, token))
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun doRefreshToken() : Flow<LoginResponse> {
        return flow{
            val response = authRemoteDataSource.doRefreshToken()
            val userInfo = response.data?: UserModel()
            val token = response.token.orEmpty()
            encryptedDataManager.setAccessToken(token)
          //  authLocalDataSource.updateToken(userInfo.id.toString(), token)
            emit(response)
        }.flowOn(ioDispatcher)
    }



    fun getUserInfo() : Flow<UserLocalData>{
        return flow{
            val userLocalData = authLocalDataSource.getUserInfo(encryptedDataManager.getAccessToken())
            emit(userLocalData)
        }.flowOn(ioDispatcher)
    }

    fun doLogout(): Flow<GeneralResponse> {
        return flow {
            val response = authRemoteDataSource.doLogout()
            encryptedDataManager.clearUserInfo()
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun doForgotPass(email: String): Flow<GeneralResponse> {
        return flow {
            val response = authRemoteDataSource.doForgotPass(email)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun doDeleteOrDeactivateAccount(reasonId: String, other: String? =null, type: String): Flow<GeneralResponse> {
        return flow {
            val response = authRemoteDataSource.doDeleteOrDeactivateAccount(reasonId, other, type)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun doDeleteOrDeactivateAccountOTP(reasonId: String, other: String? =null, type: String, otp: String): Flow<GeneralResponse> {
        return flow {
            val response = authRemoteDataSource.doDeleteOrDeactivateAccountOTP(reasonId, other, type, otp)
            encryptedDataManager.clearUserInfo()
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun getReasonsList(): Flow<ReasonsResponse> {
        return flow {
            val response = authRemoteDataSource.getReasonsList()
            emit(response)
        }.flowOn(ioDispatcher)
    }
}