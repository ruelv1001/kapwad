package dswd.ziacare.app.ui.group.viewmodel

import androidx.paging.PagingData
import dswd.ziacare.app.data.model.ErrorsData
import dswd.ziacare.app.data.repositories.member.response.ApproveRequestResponse
import dswd.ziacare.app.data.repositories.member.response.JoinGroupResponse
import dswd.ziacare.app.data.repositories.member.response.MemberListData
import dswd.ziacare.app.data.repositories.member.response.PendingMemberData
import dswd.ziacare.app.data.repositories.wallet.response.QRData
import dswd.ziacare.app.ui.main.viewmodel.GroupListViewState
import dswd.ziacare.app.ui.wallet.viewmodel.WalletViewState
import dswd.ziacare.app.utils.PopupErrorState

sealed class MemberViewState {

    object Loading : MemberViewState()
    data class SuccessGetPendingRequest(val pagingData: PagingData<PendingMemberData>) : MemberViewState()

    data class SuccessApproveJoinRequest(val approveRequestResponse: ApproveRequestResponse? = null): MemberViewState()
    data class SuccessRejectJoinRequest(val message: String = ""): MemberViewState()

    data class SuccessJoinGroup(val data: JoinGroupResponse? = null): MemberViewState()
    data class SuccessLeaveGroup(val message: String = ""): MemberViewState()
    data class SuccessGetListOfMembers(val pagingData: PagingData<MemberListData>) : MemberViewState()

    data class SuccessSearchUser(val listData: List<QRData>) : MemberViewState()
    data class SuccessInviteMember(val data: JoinGroupResponse? = null): MemberViewState()

    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : MemberViewState()
    data class InputError(val errorData: ErrorsData? = null) : MemberViewState()

}