package kapwad.reader.app.data.repositories.geotagging

import kapwad.reader.app.data.repositories.baseresponse.GeneralResponse
import kapwad.reader.app.data.repositories.geotagging.request.GeotaggingUploadRequest
import kapwad.reader.app.data.repositories.geotagging.response.GeoTaggingResponse

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

class GeoTaggingRemoteDataSource @Inject constructor(private val geoTaggingService: GeoTaggingService, private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) {









    suspend fun doUploadImage(request: GeotaggingUploadRequest): GeneralResponse {
        // Ensure video file is not null
        val videoPart = request.image?.let {
            MultipartBody.Part.createFormData(
                "image",
                it.name,
                it.asNetWorkRequestBody(IMAGE_MIME_TYPE)
            )
        } ?: throw IllegalArgumentException("Video file is required")

        val response = geoTaggingService.doUploadImage(
            videoPart,
            MultipartBody.Part.createFormData("land_area", request.land_area),
            MultipartBody.Part.createFormData("remarks", request.remarks)
        )

        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }

        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    fun getGetaggingStatus(): Flow<GeoTaggingResponse> {
        return flow {
            val response = geoTaggingService.doGetGeotaggingStatus()
            if (response.code() != HttpURLConnection.HTTP_OK) {
                throw HttpException(response)
            }
            emit(response.body() ?: throw NullPointerException("Response data is empty"))
        }.flowOn(ioDispatcher)
    }



    companion object {
        private const val VIDEO_MIME_TYPE = "video/*"
        private const val IMAGE_MIME_TYPE = "image/*"
        private const val DOCS_MIME_TYPE = "application/*"

    }

}