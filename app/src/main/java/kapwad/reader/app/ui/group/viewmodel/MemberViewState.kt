package kapwad.reader.app.ui.group.viewmodel

import androidx.paging.PagingData
import kapwad.reader.app.data.model.ErrorsData
import kapwad.reader.app.data.repositories.member.response.ApproveRequestResponse
import kapwad.reader.app.data.repositories.member.response.JoinGroupResponse
import kapwad.reader.app.data.repositories.member.response.MemberListData
import kapwad.reader.app.data.repositories.member.response.PendingMemberData
import kapwad.reader.app.data.repositories.wallet.response.QRData
import kapwad.reader.app.utils.PopupErrorState

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