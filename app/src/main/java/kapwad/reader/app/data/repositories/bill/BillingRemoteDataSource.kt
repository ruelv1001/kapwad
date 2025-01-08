package kapwad.reader.app.data.repositories.bill


import kapwad.reader.app.data.model.ConsumerListModelData
import kapwad.reader.app.data.model.CreatedBillListModelData
import kapwad.reader.app.data.repositories.bill.response.BillUploadedResponse
import kapwad.reader.app.data.repositories.crops.request.CropDetailsRequest
import kapwad.reader.app.data.repositories.crops.response.CropDetailsResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.IOException
import java.net.HttpURLConnection
import javax.inject.Inject

class BillingRemoteDataSource @Inject constructor(
    private val billingService: BillingService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

fun getUpload(upload: String): Flow<BillUploadedResponse> {
    return flow {

        val formBody = FormBody.Builder()
            .add("uploadjson", upload)
            .build()

        val response = billingService.doUpload(formBody)
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        emit(response.body() ?: throw NullPointerException("Response data is empty"))
    }.flowOn(ioDispatcher)
}


}