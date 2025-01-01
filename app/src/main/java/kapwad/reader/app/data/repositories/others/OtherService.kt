package kapwad.reader.app.data.repositories.waterrate

import kapwad.reader.app.data.model.ConsumerListModelData
import kapwad.reader.app.data.model.CreatedBillListModelData
import kapwad.reader.app.data.model.RateAListModelData
import kapwad.reader.app.data.model.RateBListModelData
import kapwad.reader.app.data.model.RateCListModelData
import kapwad.reader.app.data.model.RateListModelData
import kapwad.reader.app.data.model.TempListModelData
import kapwad.reader.app.data.repositories.crops.response.CropsResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

import retrofit2.Call


interface RateService {

    @GET("fetch_consumers.php")
    fun fetchConsumers(): Call<List<TempListModelData>>



    @GET("http://192.168.0.105/kapatagan/wr_commercial.php")
    suspend fun doGetAllRateList(): Response<List<RateListModelData>>

    @GET("http://192.168.0.105/kapatagan/wr_commercial_a.php")
    suspend fun doGetAllRateAList(): Response<List<RateAListModelData>>


    @GET("http://192.168.0.105/kapatagan/wr_commercial_b.php")
    suspend fun doGetAllRateBList(): Response<List<RateBListModelData>>


    @GET("http://192.168.0.105/kapatagan/wr_commercial_c.php")
    suspend fun doGetAllRateCList(): Response<List<RateCListModelData>>
}