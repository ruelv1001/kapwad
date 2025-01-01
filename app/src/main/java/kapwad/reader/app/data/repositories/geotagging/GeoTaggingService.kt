package kapwad.reader.app.data.repositories.geotagging

import kapwad.reader.app.data.repositories.baseresponse.GeneralResponse
import kapwad.reader.app.data.repositories.geotagging.response.GeoTaggingResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface GeoTaggingService {





    @POST("api/profile/geotagging/status")
    suspend fun doGetGeotaggingStatus(): Response<GeoTaggingResponse>



    @Multipart
    @POST("api/profile/geotagging/upload")
    suspend fun doUploadImage(
        @Part image: MultipartBody.Part,
        @Part land_area: MultipartBody.Part,
        @Part remarks: MultipartBody.Part
    ): Response<GeneralResponse>
}