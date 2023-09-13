package com.lionscare.app.data.repositories.auth

import com.lionscare.app.data.local.UserLocalData
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
            encryptedDataManager.setUserBasicInfo(userInfo)
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
            authLocalDataSource.updateToken(userInfo.id.toString(), token)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    private fun setUpUserLocalData(user : UserModel, token : String) : UserLocalData {
        return UserLocalData(
            user_id = user.id,
            qrcode = user.qrcode,
            qr_value_type = user.qr_value?.type,
            qr_value_value = user.qr_value?.value,
            qr_value_signature = user.qr_value?.signature,
            qrcode_value = user.qrcode_value,
            name = user.name,
            firstname = user.firstname,
            middlename = user.middlename,
            lastname = user.lastname,
            email = user.email,
            phone_number = user.phone_number,
            address = user.address,
            province_name = user.province_name,
            province_sku = user.province_sku,
            city_name = user.city_name,
            city_code = user.city_code,
            brgy_name = user.brgy_name,
            brgy_code = user.brgy_code,
            street_name = user.street_name,
            zipcode = user.zipcode,
            date_registered_date_db = user.date_registered?.date_db,
            date_registered_date_only = user.date_registered?.date_only,
            date_registered_time_passed = user.date_registered?.time_passed,
            date_registered_timestamp = user.date_registered?.timestamp,
            date_registered_iso_format = user.date_registered?.iso_format,
            date_registered_month_year = user.date_registered?.month_year,

            avatar_filename = user.avatar?.filename,
            avatar_path = user.avatar?.path,
            avatar_directory = user.avatar?.directory,
            avatar_full_path = user.avatar?.full_path,
            avatar_thumb_path = user.avatar?.thumb_path,

            access_token = token
        )
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
}