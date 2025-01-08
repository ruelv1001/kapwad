package kapwad.reader.app.data.repositories.meter


import kapwad.reader.app.data.model.ConsumerListModelData
import kapwad.reader.app.data.model.MeterReaderListModelData
import kapwad.reader.app.data.model.OtherListModelData
import kapwad.reader.app.data.model.RateAListModelData
import kapwad.reader.app.data.model.RateBListModelData
import kapwad.reader.app.data.model.RateCListModelData
import kapwad.reader.app.data.model.RateListModelData
import kapwad.reader.app.data.model.TempListModelData
import kapwad.reader.app.data.repositories.billing.BillService
import kapwad.reader.app.data.repositories.crops.request.CropItemListRequest
import kapwad.reader.app.data.repositories.crops.response.CropItemListResponse
import retrofit2.HttpException
import java.net.HttpURLConnection
import javax.inject.Inject

class MeterRemoteDataSource @Inject constructor(private val meterService: MeterService) {

    suspend fun getMeterList(): List<MeterReaderListModelData> {

        val response = meterService.doGetAllMeterList()
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }



}