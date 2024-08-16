package da.farmer.app.ui.group.viewmodel

import androidx.paging.PagingData
import da.farmer.app.data.model.ErrorsData
import da.farmer.app.data.repositories.member.response.ApproveRequestResponse
import da.farmer.app.data.repositories.member.response.JoinGroupResponse
import da.farmer.app.data.repositories.member.response.MemberListData
import da.farmer.app.data.repositories.member.response.PendingMemberData
import da.farmer.app.data.repositories.wallet.response.QRData
import da.farmer.app.ui.main.viewmodel.GroupListViewState
import da.farmer.app.ui.wallet.viewmodel.WalletViewState
import da.farmer.app.utils.PopupErrorState

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