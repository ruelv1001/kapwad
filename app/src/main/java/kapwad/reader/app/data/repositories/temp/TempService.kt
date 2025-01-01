package kapwad.reader.app.data.repositories.consumers

import kapwad.reader.app.data.model.ConsumerListModelData
import kapwad.reader.app.data.model.CreatedBillListModelData
import kapwad.reader.app.data.repositories.crops.response.CropsResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

import retrofit2.Call


interface ConsumerService {

    @GET("fetch_consumers.php")
    fun fetchConsumers(): Call<List<ConsumerListModelData>>



    @GET("http://192.168.0.101/kapatagan/consumers.php")
    suspend fun doGetAllConsumerList(): Response<List<ConsumerListModelData>>
}