package com.lionscare.app.data.repositories.profile

import com.lionscare.app.data.repositories.auth.response.LoginResponse
import com.lionscare.app.data.repositories.profile.request.UpdateInfoRequest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
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

    companion object {
        private const val IMAGE_MIME_TYPE = "image/*"
    }

}