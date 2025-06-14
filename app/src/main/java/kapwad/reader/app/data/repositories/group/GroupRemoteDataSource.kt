package kapwad.reader.app.data.repositories.group

import kapwad.reader.app.data.repositories.baseresponse.GeneralResponse
import kapwad.reader.app.data.repositories.group.request.CreateGroupRequest
import kapwad.reader.app.data.repositories.group.request.GetGroupListRequest
import kapwad.reader.app.data.repositories.group.response.CreateGroupResponse
import kapwad.reader.app.data.repositories.group.response.GetGroupListResponse
import kapwad.reader.app.data.repositories.group.response.ImmediateFamilyResponse
import kapwad.reader.app.data.repositories.group.response.PendingGroupRequestsListResponse
import kapwad.reader.app.data.repositories.wallet.request.SearchUserRequest
import kapwad.reader.app.data.repositories.wallet.response.SearchGroupResponse
import kapwad.reader.app.utils.asNetWorkRequestBody
import okhttp3.MultipartBody
import retrofit2.HttpException
import java.io.File
import java.net.HttpURLConnection
import javax.inject.Inject

class GroupRemoteDataSource @Inject constructor(private val groupService: GroupService)  {

    suspend fun doCreateGroup(createGroupRequest: CreateGroupRequest): CreateGroupResponse {
        val response = groupService.doCreateGroup(createGroupRequest)
        if (response.code() != HttpURLConnection.HTTP_CREATED) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun doUpdateGroup(createGroupRequest: CreateGroupRequest): CreateGroupResponse {
        val response = groupService.doUpdateGroup(createGroupRequest)
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun doShowGroup(createGroupRequest: CreateGroupRequest): CreateGroupResponse {
        val response = groupService.doShowGroup(createGroupRequest)
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun doGetImmediateFamily(): ImmediateFamilyResponse {
        //val request = GetGroupListRequest(page.toInt(), perPage.toInt())
        val response = groupService.doGetImmediateFamily()

        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }
    suspend fun doGetGroupList(page: String, perPage: String): GetGroupListResponse {
        val request = GetGroupListRequest(page.toInt(), perPage.toInt())
        val response = groupService.doGetGroupList(request)

        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun doGetPendingGroupRequest(page: String?, perPage: String?): PendingGroupRequestsListResponse {
        val request = GetGroupListRequest(page?.toInt(), perPage?.toInt())
        val response = groupService.doGetPendingGroupRequest(request)

        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun doSearchGroup(keyword: String): SearchGroupResponse {
        val request = SearchUserRequest(keyword)
        val response = groupService.doSearchGroup(request)

        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun uploadGroupAvatar(imageFile: File, group_id: String): GeneralResponse {
        val response = groupService.uploadGroupAvatar(
            MultipartBody.Part.createFormData(
                "image",
                imageFile.name,
                imageFile.asNetWorkRequestBody(IMAGE_MIME_TYPE)
            ),
            MultipartBody.Part.createFormData("group_id", group_id)
        )
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }

        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    companion object {
        private const val IMAGE_MIME_TYPE = "image/*"
    }
}