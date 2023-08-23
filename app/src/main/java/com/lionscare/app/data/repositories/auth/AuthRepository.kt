package com.lionscare.app.data.repositories.auth

import com.lionscare.app.data.repositories.baseresponse.GeneralResponse
import com.lionscare.app.data.repositories.auth.response.LoginResponse
import com.lionscare.app.data.repositories.baseresponse.UserModel
import com.lionscare.app.security.AuthEncryptedDataManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

//class AuthRepository @Inject constructor(
//    private val authRemoteDataSource: AuthRemoteDataSource,
//    private val encryptedDataManager: AuthEncryptedDataManager,
//    private val authLocalDataSource: AuthLocalDataSource,
//    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
//) {
//
//    fun doLogin(email: String, password: String): Flow<LoginResponse> {
//        return flow {
//            val response = authRemoteDataSource.doLogin(email, password)
//            val userInfo = response.data?: UserModel()
//            val token = response.token.orEmpty()
//            encryptedDataManager.setAccessToken(token)
//            encryptedDataManager.setUserBasicInfo(userInfo)
//            emit(response)
//        }.flowOn(ioDispatcher)
//    }
//}

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
            authLocalDataSource.login(setUpUserLocalData(userInfo, token))
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun doRefreshToken() : Flow<LoginResponse> {
        return flow{
            val response = authRemoteDataSource.doRefreshToken()
            val userInfo = response.data?: UserModel()
            val token = response.token.orEmpty()
            encryptedDataManager.setAccessToken(token)
//            authLocalDataSource.updateToken(userInfo.id?, token)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    private fun setUpUserLocalData(user : UserModel, token : String) : com.lionscare.app.data.local.UserLocalData {
        return com.lionscare.app.data.local.UserLocalData(
            email = user.email,
            firstname = user.firstname,
            lastname = user.lastname,
            middlename = user.middlename,
            user_id = user.id,
            access_token = token
        )
    }

    fun getUserInfo() : Flow<com.lionscare.app.data.local.UserLocalData>{
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
}