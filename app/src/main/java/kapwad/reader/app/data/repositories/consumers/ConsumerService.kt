package kapwad.reader.app.data.repositories.bill

import kapwad.reader.app.data.model.CreatedBillListModelData
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST


interface BillingService {

        @POST("syncBills.php")
        suspend fun syncBills(@Body bills: List<CreatedBillListModelData>): Response<String>


    // Retrofit instance
    object ApiClient {
        private const val BASE_URL = "https://your-server-url.com/api/"

        val instance: BillingService by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BillingService::class.java)
        }
    }
}