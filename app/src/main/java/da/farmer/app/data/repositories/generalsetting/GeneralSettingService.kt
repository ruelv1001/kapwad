package da.farmer.app.data.repositories.generalsetting

import da.farmer.app.data.repositories.generalsetting.response.RequestAssistanceLOVResponse
import da.farmer.app.data.repositories.group.request.CreateGroupRequest
import da.farmer.app.data.repositories.group.request.GetGroupListRequest
import da.farmer.app.data.repositories.group.response.CreateGroupResponse
import da.farmer.app.data.repositories.group.response.GetGroupListResponse
import da.farmer.app.data.repositories.group.response.ImmediateFamilyResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface GeneralSettingService {

    @POST("api/setting/lov/request-assistance")
    suspend fun doGetRequestAssistanceReasons(): Response<RequestAssistanceLOVResponse>
}