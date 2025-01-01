package kapwad.reader.app.ui.group.viewmodel

import kapwad.reader.app.data.model.ErrorsData
import kapwad.reader.app.data.repositories.group.response.CreateGroupResponse
import kapwad.reader.app.data.repositories.group.response.GroupData
import kapwad.reader.app.data.repositories.group.response.PendingGroupRequestsListResponse
import kapwad.reader.app.utils.PopupErrorState

sealed class GroupViewState{

    object Loading : GroupViewState()
    data class SuccessCreateGroup(val createGroupResponse: CreateGroupResponse? = null) : GroupViewState()
    data class SuccessUpdateGroup(val createGroupResponse: CreateGroupResponse? = null) : GroupViewState()
    data class SuccessShowGroup(val createGroupResponse: CreateGroupResponse? = null) : GroupViewState()
    data class SuccessUploadAvatar(val message : String = "") : GroupViewState()
    data class SuccessPendingGroupListCount(val pendingGroupRequestsListResponse : PendingGroupRequestsListResponse) : GroupViewState()


    data class SuccessSearchGroup(val listData: List<GroupData>) : GroupViewState()
    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : GroupViewState()
    data class InputError(val errorData: ErrorsData? = null) : GroupViewState()

}
