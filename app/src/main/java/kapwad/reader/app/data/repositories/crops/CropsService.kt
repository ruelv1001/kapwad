package kapwad.reader.app.data.repositories.crops

import kapwad.reader.app.data.repositories.baseresponse.GeneralResponse
import kapwad.reader.app.data.repositories.crops.request.CropDetailsRequest
import kapwad.reader.app.data.repositories.crops.request.CropItemListRequest
import kapwad.reader.app.data.repositories.crops.response.CropsResponse
import kapwad.reader.app.data.repositories.crops.response.CropDetailsResponse
import kapwad.reader.app.data.repositories.crops.response.CropItemListResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface CropsService {



    @POST("api/crops/list")
    suspend fun doGetAllCropList(): Response<CropsResponse>


    @POST("api/crops/items/list")
    suspend fun doGetAllItemsList(@Body request: CropItemListRequest): Response<CropItemListResponse>

    @POST("api/crops/items/details")
    suspend fun doGetItemDetails(@Body cropDetailsRequest: CropDetailsRequest): Response<CropDetailsResponse>

    @POST("api/crops/start")
    suspend fun doStartCrop(): Response<GeneralResponse>

    @Multipart
    @POST("api/crops/items/upload-video")
    suspend fun doUploadVideo(
        @Part video: MultipartBody.Part,
        @Part type: MultipartBody.Part,
        @Part itemId: MultipartBody.Part
    ): Response<GeneralResponse>

    @Multipart
    @POST("api/crops/items/upload-image")
    suspend fun doUploadImage(
        @Part video: MultipartBody.Part,
        @Part type: MultipartBody.Part,
        @Part itemId: MultipartBody.Part
    ): Response<GeneralResponse>
}