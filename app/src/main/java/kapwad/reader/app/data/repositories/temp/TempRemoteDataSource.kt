package kapwad.reader.app.data.repositories.consumers


import kapwad.reader.app.data.model.ConsumerListModelData
import kapwad.reader.app.data.repositories.billing.BillService
import kapwad.reader.app.data.repositories.crops.request.CropItemListRequest
import kapwad.reader.app.data.repositories.crops.response.CropItemListResponse
import retrofit2.HttpException
import java.net.HttpURLConnection
import javax.inject.Inject

class ConsumerRemoteDataSource @Inject constructor(private val consumerService: ConsumerService) {

    suspend fun getConsumerList(): List<ConsumerListModelData> {

        val response = consumerService.doGetAllConsumerList()
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }
}