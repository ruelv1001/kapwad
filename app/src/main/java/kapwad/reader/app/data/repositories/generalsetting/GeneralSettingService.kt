package kapwad.reader.app.data.repositories.generalsetting

import kapwad.reader.app.data.repositories.generalsetting.response.RequestAssistanceLOVResponse
import retrofit2.Response
import retrofit2.http.POST

interface GeneralSettingService {

    @POST("api/setting/lov/request-assistance")
    suspend fun doGetRequestAssistanceReasons(): Response<RequestAssistanceLOVResponse>
}