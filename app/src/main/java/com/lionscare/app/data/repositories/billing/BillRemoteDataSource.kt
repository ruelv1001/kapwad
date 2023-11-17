package com.lionscare.app.data.repositories.billing

import com.lionscare.app.data.repositories.baseresponse.GeneralResponse
import com.lionscare.app.data.repositories.billing.response.BillListResponse
import com.lionscare.app.data.repositories.group.request.CreateGroupRequest
import com.lionscare.app.data.repositories.group.request.GetGroupListRequest
import com.lionscare.app.data.repositories.group.response.CreateGroupResponse
import com.lionscare.app.data.repositories.group.response.GetGroupListResponse
import com.lionscare.app.data.repositories.group.response.ImmediateFamilyResponse
import com.lionscare.app.data.repositories.group.response.PendingGroupRequestsListResponse
import com.lionscare.app.data.repositories.profile.ProfileRemoteDataSource
import com.lionscare.app.data.repositories.profile.request.ProfileAvatarRequest
import com.lionscare.app.data.repositories.wallet.request.SearchUserRequest
import com.lionscare.app.data.repositories.wallet.response.SearchGroupResponse
import com.lionscare.app.utils.asNetWorkRequestBody
import okhttp3.MultipartBody
import retrofit2.HttpException
import java.io.File
import java.net.HttpURLConnection
import javax.inject.Inject

class BillRemoteDataSource @Inject constructor(private val billService: BillService)  {

    suspend fun doGetAllBillList(): BillListResponse {
        val response = billService.doGetAllBillList()

        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

}