package kapwad.reader.app.data.repositories.profile

import kapwad.reader.app.data.repositories.auth.response.LoginResponse
import kapwad.reader.app.data.repositories.baseresponse.GeneralResponse
import kapwad.reader.app.data.repositories.profile.request.BadgeRequest
import kapwad.reader.app.data.repositories.profile.request.ChangePassRequest
import kapwad.reader.app.data.repositories.profile.request.FaceIDRequest
import kapwad.reader.app.data.repositories.profile.request.KYCRequest
import kapwad.reader.app.data.repositories.profile.request.NotificationListRequest
import kapwad.reader.app.data.repositories.profile.request.ProfileAvatarRequest
import kapwad.reader.app.data.repositories.profile.request.UpdateInfoRequest
import kapwad.reader.app.data.repositories.profile.request.UpdatePhoneNumberOTPRequest
import kapwad.reader.app.data.repositories.profile.request.UpdatePhoneNumberRequest
import kapwad.reader.app.data.repositories.profile.response.BadgeResponse
import kapwad.reader.app.data.repositories.profile.response.LOVResponse
import kapwad.reader.app.data.repositories.profile.response.ProfileVerificationResponse
import kapwad.reader.app.data.repositories.profile.response.UserNotificationListResponse
import kapwad.reader.app.data.repositories.profile.response.UserNotificationResponse
import kapwad.reader.app.utils.asNetWorkRequestBody
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import retrofit2.HttpException
import java.net.HttpURLConnection
import javax.inject.Inject

class ProfileRemoteDataSource @Inject constructor(
    private val profileService: ProfileService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    fun getProfileInfo(): Flow<LoginResponse> {
        return flow {
            val response = profileService.getProfileInfo()
            if (response.code() != HttpURLConnection.HTTP_OK) {
                throw HttpException(response)
            }
            emit(response.body() ?: throw NullPointerException("Response data is empty"))
        }.flowOn(ioDispatcher)
    }

    fun doUpdateInfo(
        request: UpdateInfoRequest
    ): Flow<LoginResponse> {
        return flow {
            val response = profileService.doUpdateInfo(request)
            if (response.code() != HttpURLConnection.HTTP_OK) {
                throw HttpException(response)
            }
            emit(response.body() ?: throw NullPointerException("Response data is empty"))
        }.flowOn(ioDispatcher)
    }


    //=============== ======================KYC AP      val request = UpdateInfoRequest(
    //                province_sku,
    //                province_name,
    //                city_sku,
    //                city_name,
    //                brgy_sku,
    //                brgy_name,
    //                street_name,
    //                zipcode,
    //                firstname,
    //                lastname,
    //                middlename,
    //                email,
    //                birthdate
    //            )I
    suspend fun doUploadId(request : KYCRequest): GeneralResponse {
        val response = profileService.doUploadId(
            MultipartBody.Part.createFormData(
                "front_id",
                request.frontImageFile.name,
                request.frontImageFile.asNetWorkRequestBody(IMAGE_MIME_TYPE)
            ),
            request.backImageFile.let {
                MultipartBody.Part.createFormData(
                    "back_id",
                    it?.name,
                    it?.asNetWorkRequestBody(IMAGE_MIME_TYPE)!!
                )
            }
        ,
            MultipartBody.Part.createFormData("type", request.idType)
        )
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }

        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun doUploadProofOfAddress(request : KYCRequest): GeneralResponse {
        val response = profileService.doUploadProofOfAddress(
            MultipartBody.Part.createFormData(
                "file",
                request.frontImageFile.name,
                request.frontImageFile.asNetWorkRequestBody(IMAGE_MIME_TYPE)
            ),
            MultipartBody.Part.createFormData("type", request.idType)
        )
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }

        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun doUploadFaceId(request : FaceIDRequest): GeneralResponse {
        val response = profileService.doUploadFacialId(
            MultipartBody.Part.createFormData(
                "front_image",
                request.front_image.name,
                request.front_image.asNetWorkRequestBody(IMAGE_MIME_TYPE)
            ),
            MultipartBody.Part.createFormData(
                "left_image",
                request.left_image.name,
                request.left_image.asNetWorkRequestBody(IMAGE_MIME_TYPE)
            ),
            MultipartBody.Part.createFormData(
                "right_image",
                request.right_image.name,
                request.right_image.asNetWorkRequestBody(IMAGE_MIME_TYPE)
            )
        )
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }

        return response.body() ?: throw NullPointerException("Response data is empty")
    }


    suspend fun getIdList(): LOVResponse {
        val response = profileService.getLOVIdList()
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }

        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun getProofOfAddressList(): LOVResponse {
        val response = profileService.getLOVProofOfAddressList()
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }

        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun getVerificationStatus(): ProfileVerificationResponse {
        val response = profileService.getVerificationStatus()
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }

        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    //==================================== Change phone number
    suspend fun changePhoneNumber(request : UpdatePhoneNumberRequest): GeneralResponse {
        val response = profileService.doEditPhoneNumber(request)
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }

        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun changePhoneNumberWithOTP(request : UpdatePhoneNumberOTPRequest): GeneralResponse {
        val response = profileService.doOTP(request)
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }

        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    //=================================BADGE API
    suspend fun doRequestBadge(request : BadgeRequest): BadgeResponse {
        val response = profileService.doRequestBadge(
            MultipartBody.Part.createFormData(
                "doc1",
                request.doc1.name,
                request.doc1.asNetWorkRequestBody(DOCS_MIME_TYPE)
            ),
            MultipartBody.Part.createFormData(
                "doc2",
                request.doc2.name,
                request.doc2.asNetWorkRequestBody(DOCS_MIME_TYPE)
            ),
            MultipartBody.Part.createFormData("type", request.type)
        )

        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }

        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    //===================== Change password

    fun doChangePass(
        currentPass: String,
        newPass: String,
        confirmPass: String
    ): Flow<GeneralResponse> {
        return flow {
            val request = ChangePassRequest(currentPass, newPass, confirmPass)
            val response = profileService.doChangePass(request)
            if (response.code() != HttpURLConnection.HTTP_OK) {
                throw HttpException(response)
            }
            emit(response.body() ?: throw NullPointerException("Response data is empty"))
        }.flowOn(ioDispatcher)
    }

    //========================upload avatar
    suspend fun uploadAvatar(request : ProfileAvatarRequest): GeneralResponse {
        val response = profileService.uploadAvatar(
            MultipartBody.Part.createFormData(
                "image",
                request.image.name,
                request.image.asNetWorkRequestBody(IMAGE_MIME_TYPE)
            )
        )
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }

        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun getUserNotificationList(per_page: Int, page : Int): UserNotificationListResponse {
        val request = NotificationListRequest(per_page = per_page, page = page)
        val response = profileService.getUserNotificationList(request)
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }

        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun getUserNotificationInfo(notifId: String): UserNotificationResponse {
        val request = NotificationListRequest(notif_id = notifId)
        val response = profileService.getUserNotificationInfo(request)
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }

        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun getGroupNotificationList(group_id: String, per_page: Int, page : Int): UserNotificationListResponse {
        val request = NotificationListRequest(group_id = group_id, per_page = per_page, page = page)
        val response = profileService.getGroupNotificationList(request)
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }

        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun getGroupNotificationInfo(group_id: String, notifId: String): UserNotificationResponse {
        val request = NotificationListRequest(group_id = group_id, notif_id = notifId)
        val response = profileService.getGroupNotificationInfo(request)
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }

        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    companion object {
        private const val IMAGE_MIME_TYPE = "image/*"
        private const val DOCS_MIME_TYPE = "application/*"

    }

}