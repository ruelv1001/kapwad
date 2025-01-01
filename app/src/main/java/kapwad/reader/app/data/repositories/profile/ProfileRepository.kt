package kapwad.reader.app.data.repositories.profile

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kapwad.reader.app.data.repositories.auth.response.LoginResponse
import kapwad.reader.app.data.repositories.baseresponse.GeneralResponse
import kapwad.reader.app.data.repositories.baseresponse.UserModel
import kapwad.reader.app.data.repositories.profile.request.BadgeRequest
import kapwad.reader.app.data.repositories.profile.request.FaceIDRequest
import kapwad.reader.app.data.repositories.profile.request.KYCRequest
import kapwad.reader.app.data.repositories.profile.request.ProfileAvatarRequest
import kapwad.reader.app.data.repositories.profile.request.UpdateInfoRequest
import kapwad.reader.app.data.repositories.profile.request.UpdatePhoneNumberOTPRequest
import kapwad.reader.app.data.repositories.profile.request.UpdatePhoneNumberRequest
import kapwad.reader.app.data.repositories.profile.response.BadgeResponse
import kapwad.reader.app.data.repositories.profile.response.LOVResponse
import kapwad.reader.app.data.repositories.profile.response.ProfileVerificationResponse
import kapwad.reader.app.data.repositories.profile.response.UserNotificationData
import kapwad.reader.app.data.repositories.profile.response.UserNotificationResponse
import kapwad.reader.app.security.AuthEncryptedDataManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
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

    fun doUploadFacialId(request: FaceIDRequest): Flow<GeneralResponse> {
        return flow {
            val response = profileRemoteDataSource.doUploadFaceId(request)
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
        pagingConfig: PagingConfig = getDefaultPageConfig()
    ): Flow<PagingData<UserNotificationData>> {
        val getUserNotificationListPagingSource =
            GetUserNotificationListPagingSource(profileRemoteDataSource)
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

    fun getGroupNotificationList(
        pagingConfig: PagingConfig = getDefaultPageConfig(),
        groupId: String
    ): Flow<PagingData<UserNotificationData>> {
        val getGroupNotificationListPagingSource =
            GetGroupNotificationListPagingSource(profileRemoteDataSource, groupId)
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = { getGroupNotificationListPagingSource }
        ).flow
            .flowOn(ioDispatcher)
    }

    fun getGroupNotificationInfo(
        groupId: String,
        notifId: String
    ): Flow<UserNotificationResponse> {
        return flow {
            val response =
                profileRemoteDataSource.getGroupNotificationInfo(groupId,notifId)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    private fun getDefaultPageConfig(): PagingConfig {
        return PagingConfig(pageSize = 5, initialLoadSize = 5, enablePlaceholders = false)
    }
}