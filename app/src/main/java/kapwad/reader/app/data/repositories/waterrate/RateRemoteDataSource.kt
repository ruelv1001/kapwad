package kapwad.reader.app.data.repositories.waterrate


import kapwad.reader.app.data.model.ConsumerListModelData
import kapwad.reader.app.data.model.RateAListModelData
import kapwad.reader.app.data.model.RateBListModelData
import kapwad.reader.app.data.model.RateCListModelData
import kapwad.reader.app.data.model.RateListModelData
import kapwad.reader.app.data.model.RateReListModelData
import kapwad.reader.app.data.model.TempListModelData
import kapwad.reader.app.data.repositories.billing.BillService
import kapwad.reader.app.data.repositories.crops.request.CropItemListRequest
import kapwad.reader.app.data.repositories.crops.response.CropItemListResponse
import retrofit2.HttpException
import java.net.HttpURLConnection
import javax.inject.Inject

class RateRemoteDataSource @Inject constructor(private val rateService: RateService) {

    suspend fun getRateList(): List<RateListModelData> {

        val response = rateService.doGetAllRateList()
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }


    suspend fun getRateAList(): List<RateAListModelData> {

        val response = rateService.doGetAllRateAList()
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }



    suspend fun getRateBList(): List<RateBListModelData> {

        val response = rateService.doGetAllRateBList()
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun getRateCList(): List<RateCListModelData> {

        val response = rateService.doGetAllRateCList()
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun getRateReList(): List<RateReListModelData> {

        val response = rateService.doGetAllRateReList()
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }
}