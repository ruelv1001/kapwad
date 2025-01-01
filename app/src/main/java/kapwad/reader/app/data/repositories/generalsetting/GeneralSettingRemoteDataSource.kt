package kapwad.reader.app.data.repositories.generalsetting

import kapwad.reader.app.data.repositories.generalsetting.response.RequestAssistanceLOVResponse
import retrofit2.HttpException
import java.net.HttpURLConnection
import javax.inject.Inject

class GeneralSettingRemoteDataSource @Inject constructor(private val generalSettingService: GeneralSettingService)  {

    suspend fun doGetRequestAssistanceReasons(): RequestAssistanceLOVResponse {
        val response = generalSettingService.doGetRequestAssistanceReasons()
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

}