package kapwad.reader.app.data.repositories.temp


import kapwad.reader.app.data.model.ConsumerListModelData
import kapwad.reader.app.data.model.TempListModelData
import kapwad.reader.app.data.repositories.billing.BillService
import kapwad.reader.app.data.repositories.crops.request.CropItemListRequest
import kapwad.reader.app.data.repositories.crops.response.CropItemListResponse
import retrofit2.HttpException
import java.net.HttpURLConnection
import javax.inject.Inject

class TempRemoteDataSource @Inject constructor(private val tempService: TempService) {

    suspend fun getTempList(): List<TempListModelData> {

        val response = tempService.doGetAllTempList()
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }
}