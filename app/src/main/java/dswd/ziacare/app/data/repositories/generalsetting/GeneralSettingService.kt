package dswd.ziacare.app.data.repositories.generalsetting

import dswd.ziacare.app.data.repositories.generalsetting.response.RequestAssistanceLOVResponse
import dswd.ziacare.app.data.repositories.group.request.CreateGroupRequest
import dswd.ziacare.app.data.repositories.group.request.GetGroupListRequest
import dswd.ziacare.app.data.repositories.group.response.CreateGroupResponse
import dswd.ziacare.app.data.repositories.group.response.GetGroupListResponse
import dswd.ziacare.app.data.repositories.group.response.ImmediateFamilyResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface GeneralSettingService {

    @POST("api/setting/lov/request-assistance")
    suspend fun doGetRequestAssistanceReasons(): Response<RequestAssistanceLOVResponse>
}