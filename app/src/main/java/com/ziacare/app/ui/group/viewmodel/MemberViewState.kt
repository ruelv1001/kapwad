package com.ziacare.app.ui.group.viewmodel

import androidx.paging.PagingData
import com.ziacare.app.data.model.ErrorsData
import com.ziacare.app.data.repositories.member.response.ApproveRequestResponse
import com.ziacare.app.data.repositories.member.response.JoinGroupResponse
import com.ziacare.app.data.repositories.member.response.MemberListData
import com.ziacare.app.data.repositories.member.response.PendingMemberData
import com.ziacare.app.data.repositories.wallet.response.QRData
import com.ziacare.app.ui.main.viewmodel.GroupListViewState
import com.ziacare.app.ui.wallet.viewmodel.WalletViewState
import com.ziacare.app.utils.PopupErrorState

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