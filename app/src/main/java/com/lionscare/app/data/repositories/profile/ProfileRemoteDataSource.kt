package com.lionscare.app.data.repositories.profile

import com.lionscare.app.data.repositories.auth.response.LoginResponse
import com.lionscare.app.data.repositories.baseresponse.GeneralResponse
import com.lionscare.app.data.repositories.profile.request.BadgeRequest
import com.lionscare.app.data.repositories.profile.request.ChangePassRequest
import com.lionscare.app.data.repositories.profile.request.KYCRequest
import com.lionscare.app.data.repositories.profile.request.UpdateInfoRequest
import com.lionscare.app.data.repositories.profile.request.UpdatePhoneNumberOTPRequest
import com.lionscare.app.data.repositories.profile.request.UpdatePhoneNumberRequest
import com.lionscare.app.data.repositories.profile.response.BadgeResponse
import com.lionscare.app.data.repositories.profile.response.BadgeStatusResponse
import com.lionscare.app.data.repositories.profile.response.LOVResponse
import com.lionscare.app.data.repositories.profile.response.ProfileVerificationResponse
import com.lionscare.app.utils.asNetWorkRequestBody
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
        email: String
    ): Flow<LoginResponse> {
        return flow {
            val request = UpdateInfoRequest(
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
            )
            val response = profileService.doUpdateInfo(request)
            if (response.code() != HttpURLConnection.HTTP_OK) {
                throw HttpException(response)
            }
            emit(response.body() ?: throw NullPointerException("Response data is empty"))
        }.flowOn(ioDispatcher)
    }


    //=============== ======================KYC API
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
    suspend fun getBadgeStatus(): BadgeStatusResponse {
        val response = profileService.getBadgeStatus()
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }

        return response.body() ?: throw NullPointerException("Response data is empty")
    }

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

    companion object {
        private const val IMAGE_MIME_TYPE = "image/*"
        private const val DOCS_MIME_TYPE = "application/*"

    }

}