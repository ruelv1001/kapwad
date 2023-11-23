package com.ziacare.app.data.repositories.profile

import com.ziacare.app.data.repositories.auth.response.LoginResponse
import com.ziacare.app.data.repositories.baseresponse.GeneralResponse
import com.ziacare.app.data.repositories.profile.request.BadgeRemovalRequest
import com.ziacare.app.data.repositories.profile.request.BadgeRequest
import com.ziacare.app.data.repositories.profile.request.ChangePassRequest
import com.ziacare.app.data.repositories.profile.request.NotificationListRequest
import com.ziacare.app.data.repositories.profile.request.UpdateInfoRequest
import com.ziacare.app.data.repositories.profile.request.UpdatePhoneNumberOTPRequest
import com.ziacare.app.data.repositories.profile.request.UpdatePhoneNumberRequest
import com.ziacare.app.data.repositories.profile.response.BadgeRemovalStatusResponse
import com.ziacare.app.data.repositories.profile.response.BadgeResponse
import com.ziacare.app.data.repositories.profile.response.BadgeStatusResponse
import com.ziacare.app.data.repositories.profile.response.LOVResponse
import com.ziacare.app.data.repositories.profile.response.ProfileVerificationResponse
import com.ziacare.app.data.repositories.profile.response.UserNotificationListResponse
import com.ziacare.app.data.repositories.profile.response.UserNotificationResponse
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

    @POST("api/profile/badge/status")
    suspend fun getBadgeStatus(): Response<BadgeStatusResponse>

    @POST("api/profile/setting/password")
    suspend fun doChangePass(@Body request : ChangePassRequest): Response<GeneralResponse>

    //================= UPload profile avatar

    @Multipart
    @POST("api/profile/setting/update-avatar")
    suspend fun uploadAvatar(@Part image: MultipartBody.Part): Response<GeneralResponse>


    //============================= Cancel Badge
    @POST("api/profile/badge/remove/request")
    suspend fun requestBadgeRemoval(@Body request : BadgeRemovalRequest): Response<GeneralResponse>

    @POST("api/profile/badge/remove/status")
    suspend fun getBadgeRemovalStatus(): Response<BadgeRemovalStatusResponse>

    @POST("api/profile/badge/remove/cancel")
    suspend fun cancelRequestBadgeRemoval(): Response<GeneralResponse>

    @POST("api/notification/user")
    suspend fun getUserNotificationList(@Body request: NotificationListRequest): Response<UserNotificationListResponse>

    @POST("api/notification/user/info")
    suspend fun getUserNotificationInfo(@Body request: NotificationListRequest): Response<UserNotificationResponse>

    @POST("api/notification/group")
    suspend fun getGroupNotificationList(@Body request: NotificationListRequest): Response<UserNotificationListResponse>

    @POST("api/notification/group/info")
    suspend fun getGroupNotificationInfo(@Body request: NotificationListRequest): Response<UserNotificationResponse>
}