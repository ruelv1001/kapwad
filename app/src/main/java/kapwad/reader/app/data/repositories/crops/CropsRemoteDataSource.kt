package kapwad.reader.app.data.repositories.crops

import com.google.gson.Gson
import kapwad.reader.app.data.repositories.baseresponse.GeneralResponse
import kapwad.reader.app.data.repositories.crops.request.CropDetailsRequest
import kapwad.reader.app.data.repositories.crops.request.CropItemListRequest
import kapwad.reader.app.data.repositories.crops.request.UploadImageRequest
import kapwad.reader.app.data.repositories.crops.response.CropsResponse
import kapwad.reader.app.data.repositories.crops.request.UploadVideRequest
import kapwad.reader.app.data.repositories.crops.response.CropDetailsResponse
import kapwad.reader.app.data.repositories.crops.response.CropItemListResponse

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

class CropsRemoteDataSource @Inject constructor(private val cropsService: CropsService,    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) {






    suspend fun doGetAllCropsList(): CropsResponse {
        val response = cropsService.doGetAllCropList()

        return if (response.isSuccessful) {
            response.body() ?: throw NullPointerException("Response data is empty")
        } else {
            if (response.code() == HttpURLConnection.HTTP_BAD_REQUEST) {
                // Parse the error body
                val errorBody = response.errorBody()?.string()
                val errorResponse = errorBody?.let {
                    // Assuming you have a data class for the error response
                    parseErrorResponse(it)
                }
                throw BadRequestException(errorResponse) // Custom exception with parsed error
            } else {
                throw HttpException(response)
            }
        }
    }
    class BadRequestException(val errorResponse: ErrorResponse?) : Exception("Bad Request: $errorResponse")

    // Your data class for the JSON structure
    data class ErrorResponse(
        val errorCode: String,
        val message: String
        // Add other fields as necessary
    )

    // Helper function to parse the error response JSON
    fun parseErrorResponse(errorBody: String): ErrorResponse {
        return try {
            // Assuming you have a data class `ErrorResponse` for parsing the JSON error
            val gson = Gson() // or use your preferred JSON parser
            gson.fromJson(errorBody, ErrorResponse::class.java)
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to parse error response", e)
        }
    }

    suspend fun getCropItemList(cropId: String): CropItemListResponse {
        val cropRequest = CropItemListRequest(
            crop_id = cropId,
        )
        val response = cropsService.doGetAllItemsList(cropRequest)
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun getStartCrop(): GeneralResponse {

        val response = cropsService.doStartCrop()
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun doUploadVideo(request: UploadVideRequest): GeneralResponse {
        // Ensure video file is not null
        val videoPart = request.video?.let {
            MultipartBody.Part.createFormData(
                "video",
                it.name,
                it.asNetWorkRequestBody(VIDEO_MIME_TYPE)
            )
        } ?: throw IllegalArgumentException("Video file is required")

        val response = cropsService.doUploadVideo(
            videoPart,
            MultipartBody.Part.createFormData("item_id", request.item_id),
            MultipartBody.Part.createFormData("type", request.type)
        )

        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }

        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun doUploadImage(request: UploadImageRequest): GeneralResponse {
        // Ensure video file is not null
        val videoPart = request.image?.let {
            MultipartBody.Part.createFormData(
                "image",
                it.name,
                it.asNetWorkRequestBody(IMAGE_MIME_TYPE)
            )
        } ?: throw IllegalArgumentException("Video file is required")

        val response = cropsService.doUploadImage(
            videoPart,
            MultipartBody.Part.createFormData("item_id", request.item_id),
            MultipartBody.Part.createFormData("type", request.type)
        )

        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }

        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    fun getCropDetails(cropDetailsRequest: CropDetailsRequest): Flow<CropDetailsResponse> {
        return flow {
            val response = cropsService.doGetItemDetails(cropDetailsRequest)
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