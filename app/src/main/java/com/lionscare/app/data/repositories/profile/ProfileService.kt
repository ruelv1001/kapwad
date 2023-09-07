package com.lionscare.app.data.repositories.profile

import com.lionscare.app.data.repositories.auth.response.LoginResponse
import com.lionscare.app.data.repositories.baseresponse.GeneralResponse
import com.lionscare.app.data.repositories.profile.request.BadgeRequest
import com.lionscare.app.data.repositories.profile.request.UpdateInfoRequest
import com.lionscare.app.data.repositories.profile.request.UpdatePhoneNumberOTPRequest
import com.lionscare.app.data.repositories.profile.request.UpdatePhoneNumberRequest
import com.lionscare.app.data.repositories.profile.response.BadgeResponse
import com.lionscare.app.data.repositories.profile.response.BadgeStatusResponse
import com.lionscare.app.data.repositories.profile.response.LOVResponse
import com.lionscare.app.data.repositories.profile.response.ProfileVerificationResponse
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



}