package kapwad.reader.app.data.repositories.bill

import kapwad.reader.app.data.model.CreatedBillListModelData
import kapwad.reader.app.data.model.MeterReaderListModelData
import kapwad.reader.app.data.repositories.bill.response.BillUploadedResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


interface BillingService {





    @POST("https://kapwd.com/API/create_bill.php")
    suspend fun doUpload(@Body uploadjson: RequestBody): Response<BillUploadedResponse>
}