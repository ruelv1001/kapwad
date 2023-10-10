package com.lionscare.app.data.repositories.profile

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.lionscare.app.data.repositories.auth.response.LoginResponse
import com.lionscare.app.data.repositories.baseresponse.GeneralResponse
import com.lionscare.app.data.repositories.baseresponse.UserModel
import com.lionscare.app.data.repositories.profile.request.BadgeRemovalRequest
import com.lionscare.app.data.repositories.profile.request.BadgeRequest
import com.lionscare.app.data.repositories.profile.request.KYCRequest
import com.lionscare.app.data.repositories.profile.request.ProfileAvatarRequest
import com.lionscare.app.data.repositories.profile.request.UpdateInfoRequest
import com.lionscare.app.data.repositories.profile.request.UpdatePhoneNumberOTPRequest
import com.lionscare.app.data.repositories.profile.request.UpdatePhoneNumberRequest
import com.lionscare.app.data.repositories.profile.response.BadgeRemovalStatusResponse
import com.lionscare.app.data.repositories.profile.response.BadgeResponse
import com.lionscare.app.data.repositories.profile.response.BadgeStatusResponse
import com.lionscare.app.data.repositories.profile.response.LOVResponse
import com.lionscare.app.data.repositories.profile.response.ProfileVerificationResponse
import com.lionscare.app.data.repositories.profile.response.UserNotificationData
import com.lionscare.app.data.repositories.profile.response.UserNotificationResponse
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
       request: UpdateInfoRequest
    ): Flow<LoginResponse> {
        return channelFlow {
            profileRemoteDataSource.doUpdateInfo(
              request
            ).collectLatest { response ->
                val userInfo = response.data ?: UserModel()
                encryptedDataManager.setUserBasicInfo(userInfo)
                send(response)
            }
        }.flowOn(ioDispatcher)
    }


    //=========== ===========================KYC API
    fun doUploadId(request: KYCRequest): Flow<GeneralResponse> {
        return flow {
            val response = profileRemoteDataSource.doUploadId(request)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun doUploadProofAddress(request: KYCRequest): Flow<GeneralResponse> {
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
    fun changePhoneNumber(request: UpdatePhoneNumberRequest): Flow<GeneralResponse> {
        return flow {
            val response = profileRemoteDataSource.changePhoneNumber(request)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun changePhoneNumberWithOTP(request: UpdatePhoneNumberOTPRequest): Flow<GeneralResponse> {
        return flow {
            val response = profileRemoteDataSource.changePhoneNumberWithOTP(request)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    //=============================== BADGE API
    fun doRequestBadge(request: BadgeRequest): Flow<BadgeResponse> {
        return flow {
            val response = profileRemoteDataSource.doRequestBadge(request)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun getBadgeStatus(): Flow<BadgeStatusResponse> {
        return flow {
            val response = profileRemoteDataSource.getBadgeStatus()
            emit(response)
        }.flowOn(ioDispatcher)
    }

    //================================= Badge Removal
    fun requestBadgeRemoval(request: BadgeRemovalRequest): Flow<GeneralResponse> {
        return flow {
            val response = profileRemoteDataSource.requestBadgeRemoval(request)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun getBadgeRemovalStatus(): Flow<BadgeRemovalStatusResponse> {
        return flow {
            val response = profileRemoteDataSource.getBadgeRemovalStatus()
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun cancelRequestBadgeRemoval(): Flow<GeneralResponse> {
        return flow {
            val response = profileRemoteDataSource.cancelRequestBadgeRemoval()
            emit(response)
        }.flowOn(ioDispatcher)
    }
    //======================= Change pass
    fun doChangePass(
        currentPass: String,
        newPass: String,
        confirmPass: String
    ): Flow<GeneralResponse> {
        return channelFlow {
            profileRemoteDataSource.doChangePass(currentPass, newPass, confirmPass)
                .collectLatest { response ->
                    send(response)
                }
        }.flowOn(ioDispatcher)
    }

    //===================upload avatar
    fun uploadAvatar(request: ProfileAvatarRequest): Flow<GeneralResponse> {
        return flow {
            val response = profileRemoteDataSource.uploadAvatar(request)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun getUserNotificationList(
        pagingConfig: PagingConfig = getDefaultPageConfig(),
        groupId: String
    ): Flow<PagingData<UserNotificationData>> {
        val getUserNotificationListPagingSource =
            GetUserNotificationListPagingSource(profileRemoteDataSource, groupId)
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = { getUserNotificationListPagingSource }
        ).flow
            .flowOn(ioDispatcher)
    }

    fun getUserNotificationInfo(
        notifId: String
    ): Flow<UserNotificationResponse> {
        return flow {
            val response =
                profileRemoteDataSource.getUserNotificationInfo(notifId)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    private fun getDefaultPageConfig(): PagingConfig {
        return PagingConfig(pageSize = 5, initialLoadSize = 5, enablePlaceholders = false)
    }
}