package com.lionscare.app.data.repositories.profile

import com.lionscare.app.data.repositories.auth.response.LoginResponse
import com.lionscare.app.data.repositories.baseresponse.GeneralResponse
import com.lionscare.app.data.repositories.profile.request.UpdateInfoRequest
import com.lionscare.app.data.repositories.profile.response.LOVResponse
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


}