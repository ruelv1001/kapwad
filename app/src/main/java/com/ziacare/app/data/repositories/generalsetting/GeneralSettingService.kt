package com.ziacare.app.data.repositories.generalsetting

import com.ziacare.app.data.repositories.generalsetting.response.RequestAssistanceLOVResponse
import com.ziacare.app.data.repositories.group.request.CreateGroupRequest
import com.ziacare.app.data.repositories.group.request.GetGroupListRequest
import com.ziacare.app.data.repositories.group.response.CreateGroupResponse
import com.ziacare.app.data.repositories.group.response.GetGroupListResponse
import com.ziacare.app.data.repositories.group.response.ImmediateFamilyResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface GeneralSettingService {

    @POST("api/setting/lov/request-assistance")
    suspend fun doGetRequestAssistanceReasons(): Response<RequestAssistanceLOVResponse>
}