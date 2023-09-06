package com.lionscare.app.data.repositories.profile

import com.lionscare.app.data.repositories.auth.response.LoginResponse
import com.lionscare.app.data.repositories.baseresponse.GeneralResponse
import com.lionscare.app.data.repositories.baseresponse.UserModel
import com.lionscare.app.data.repositories.profile.request.KYCRequest
import com.lionscare.app.data.repositories.profile.request.UpdatePhoneNumberOTPRequest
import com.lionscare.app.data.repositories.profile.request.UpdatePhoneNumberRequest
import com.lionscare.app.data.repositories.profile.response.LOVResponse
import com.lionscare.app.data.repositories.profile.response.ProfileVerificationResponse
import com.lionscare.app.security.AuthEncryptedDataManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
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
        middlename: String,
        email : String
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
                middlename,
                email
            ).collectLatest { response ->
                val userInfo = response.data ?: UserModel()
                encryptedDataManager.setUserBasicInfo(userInfo)
                send(response)
            }
        }.flowOn(ioDispatcher)
    }


    //=========== ===========================KYC API
    fun doUploadId(request : KYCRequest): Flow<GeneralResponse> {
        return flow {
            val response = profileRemoteDataSource.doUploadId(request)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun doUploadProofAddress(request : KYCRequest): Flow<GeneralResponse> {
        return flow {
            val response = profileRemoteDataSource.doUploadProofOfAddress(request)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun getIdList(): Flow<LOVResponse> {
        return flow {
            val response = profileRemoteDataSource.getIdList()
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun getProofOfAddressList(): Flow<LOVResponse> {
        return flow {
            val response = profileRemoteDataSource.getProofOfAddressList()
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun getVerificationStatus(): Flow<ProfileVerificationResponse> {
        return flow {
            val response = profileRemoteDataSource.getVerificationStatus()
            emit(response)
        }.flowOn(ioDispatcher)
    }

    //=============================CHange phonenumber

    fun changePhoneNumber(request : UpdatePhoneNumberRequest): Flow<GeneralResponse> {
        return flow {
            val response = profileRemoteDataSource.changePhoneNumber(request)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun changePhoneNumberWithOTP(request : UpdatePhoneNumberOTPRequest): Flow<GeneralResponse> {
        return flow {
            val response = profileRemoteDataSource.changePhoneNumberWithOTP(request)
            emit(response)
        }.flowOn(ioDispatcher)
    }


}