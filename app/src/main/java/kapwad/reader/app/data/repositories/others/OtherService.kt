package kapwad.reader.app.data.repositories.others

import kapwad.reader.app.data.model.ConsumerListModelData
import kapwad.reader.app.data.model.CreatedBillListModelData
import kapwad.reader.app.data.model.OtherListModelData
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


interface OtherService {

    @GET("fetch_consumers.php")
    fun fetchConsumers(): Call<List<TempListModelData>>



    @GET("https://kapwd.com/API/other_charges.php")
    suspend fun doGetAllOtherList(): Response<List<OtherListModelData>>

}