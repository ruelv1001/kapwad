package dswd.ziacare.app.ui.group.viewmodel

import dswd.ziacare.app.data.model.ErrorsData
import dswd.ziacare.app.data.repositories.group.response.CreateGroupResponse
import dswd.ziacare.app.data.repositories.group.response.GetGroupListResponse
import dswd.ziacare.app.data.repositories.group.response.GroupData
import dswd.ziacare.app.data.repositories.group.response.GroupListData
import dswd.ziacare.app.data.repositories.group.response.PendingGroupRequestsListResponse
import dswd.ziacare.app.ui.main.viewmodel.GroupListViewState
import dswd.ziacare.app.ui.profile.viewmodel.ProfileViewState
import dswd.ziacare.app.ui.wallet.viewmodel.WalletViewState
import dswd.ziacare.app.utils.PopupErrorState

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
