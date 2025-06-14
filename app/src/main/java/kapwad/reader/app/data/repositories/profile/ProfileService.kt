package kapwad.reader.app.data.repositories.profile

import kapwad.reader.app.data.repositories.auth.response.LoginResponse
import kapwad.reader.app.data.repositories.baseresponse.GeneralResponse
import kapwad.reader.app.data.repositories.profile.request.ChangePassRequest
import kapwad.reader.app.data.repositories.profile.request.NotificationListRequest
import kapwad.reader.app.data.repositories.profile.request.UpdateInfoRequest
import kapwad.reader.app.data.repositories.profile.request.UpdatePhoneNumberOTPRequest
import kapwad.reader.app.data.repositories.profile.request.UpdatePhoneNumberRequest
import kapwad.reader.app.data.repositories.profile.response.BadgeResponse
import kapwad.reader.app.data.repositories.profile.response.LOVResponse
import kapwad.reader.app.data.repositories.profile.response.ProfileVerificationResponse
import kapwad.reader.app.data.repositories.profile.response.UserNotificationListResponse
import kapwad.reader.app.data.repositories.profile.response.UserNotificationResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ProfileService {
    @POST("api/profile/setting/info")
    suspend fun getProfileInfo(): Response<LoginResponse>

    @POST("api/profile/setting/update-info")
    suspend fun doUpdateInfo(@Body request: UpdateInfoRequest): Response<LoginResponse>

    //============================================================================ KYC API
    @Multipart
    @POST("api/profile/kyc/id")
    suspend fun doUploadId(
        @Part frontImagePart: MultipartBody.Part,
        @Part backImagePart: MultipartBody.Part,
        @Part idTypePart: MultipartBody.Part
    ): Response<GeneralResponse>

    @Multipart
    @POST("api/profile/kyc/address")
    suspend fun doUploadProofOfAddress(
        @Part imagePart: MultipartBody.Part,
        @Part proof_type: MultipartBody.Part
    ): Response<GeneralResponse>


    @Multipart
    @POST("api/profile/kyc/facial-id")
    suspend fun doUploadFacialId(
        @Part front_image: MultipartBody.Part,
        @Part left_image: MultipartBody.Part,
        @Part right_image: MultipartBody.Part,
    ): Response<GeneralResponse>

    @POST("api/setting/lov/id")
    suspend fun getLOVIdList(): Response<LOVResponse>

    @POST("api/setting/lov/address")
    suspend fun getLOVProofOfAddressList(): Response<LOVResponse>


    @POST("api/profile/kyc/status")
    suspend fun getVerificationStatus(): Response<ProfileVerificationResponse>


    //================================PHONE NUMBER OTP
    @POST("api/profile/phone-number/edit")
    suspend fun doEditPhoneNumber(@Body request : UpdatePhoneNumberRequest): Response<GeneralResponse>

    @POST("api/profile/phone-number/otp")
    suspend fun doOTP(@Body request : UpdatePhoneNumberOTPRequest): Response<GeneralResponse>


    //=================================BADGE API
    @Multipart
    @POST("api/profile/badge/apply")
    suspend fun doRequestBadge(
        @Part doc1: MultipartBody.Part,
        @Part doc2: MultipartBody.Part,
        @Part type: MultipartBody.Part
    ): Response<BadgeResponse>

    @POST("api/profile/setting/password")
    suspend fun doChangePass(@Body request : ChangePassRequest): Response<GeneralResponse>

    //================= UPload profile avatar

    @Multipart
    @POST("api/profile/setting/update-avatar")
    suspend fun uploadAvatar(@Part image: MultipartBody.Part): Response<GeneralResponse>

    @POST("api/notification/user")
    suspend fun getUserNotificationList(@Body request: NotificationListRequest): Response<UserNotificationListResponse>

    @POST("api/notification/user/info")
    suspend fun getUserNotificationInfo(@Body request: NotificationListRequest): Response<UserNotificationResponse>

    @POST("api/notification/group")
    suspend fun getGroupNotificationList(@Body request: NotificationListRequest): Response<UserNotificationListResponse>

    @POST("api/notification/group/info")
    suspend fun getGroupNotificationInfo(@Body request: NotificationListRequest): Response<UserNotificationResponse>
}