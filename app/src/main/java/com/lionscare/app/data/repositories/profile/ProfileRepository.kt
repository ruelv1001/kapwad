package com.lionscare.app.data.repositories.profile

import com.lionscare.app.data.repositories.auth.response.LoginResponse
import com.lionscare.app.data.repositories.baseresponse.UserModel
import com.lionscare.app.security.AuthEncryptedDataManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val profileRemoteDataSource: ProfileRemoteDataSource,
    private val encryptedDataManager: AuthEncryptedDataManager,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    fun getProfileInfo(): Flow<LoginResponse> {
        return channelFlow {
            profileRemoteDataSource.getProfileInfo().collectLatest { response ->
                val userInfo = response.data ?: UserModel()
                encryptedDataManager.setUserBasicInfo(userInfo)
                send(response)
            }
        }.flowOn(ioDispatcher)
    }

    fun doUpdateInfo(
        province_sku: String,
        province_name: String,
        city_sku: String,
        city_name: String,
        brgy_sku: String,
        brgy_name: String,
        street_name: String,
        zipcode: String,
        firstname: String,
        lastname: String,
        middlename: String
    ): Flow<LoginResponse> {
        return channelFlow {
            profileRemoteDataSource.doUpdateInfo(
                province_sku,
                province_name,
                city_sku,
                city_name,
                brgy_sku,
                brgy_name,
                street_name,
                zipcode,
                firstname,
                lastname,
                middlename
            ).collectLatest { response ->
                val userInfo = response.data ?: UserModel()
                encryptedDataManager.setUserBasicInfo(userInfo)
                send(response)
            }
        }.flowOn(ioDispatcher)
    }

}