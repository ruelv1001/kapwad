package da.farmer.app.ui.group.viewmodel

import da.farmer.app.data.model.ErrorsData
import da.farmer.app.data.repositories.group.response.CreateGroupResponse
import da.farmer.app.data.repositories.group.response.GetGroupListResponse
import da.farmer.app.data.repositories.group.response.GroupData
import da.farmer.app.data.repositories.group.response.GroupListData
import da.farmer.app.data.repositories.group.response.PendingGroupRequestsListResponse
import da.farmer.app.ui.main.viewmodel.GroupListViewState
import da.farmer.app.ui.profile.viewmodel.ProfileViewState
import da.farmer.app.ui.wallet.viewmodel.WalletViewState
import da.farmer.app.utils.PopupErrorState

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
