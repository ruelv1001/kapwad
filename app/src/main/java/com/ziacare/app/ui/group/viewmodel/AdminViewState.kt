package com.ziacare.app.ui.group.viewmodel

import androidx.paging.PagingData
import com.ziacare.app.data.model.ErrorsData
import com.ziacare.app.data.repositories.member.response.MemberListData
import com.ziacare.app.utils.PopupErrorState

sealed class AdminViewState {

    object Loading : AdminViewState()
    data class SuccessGetListOfAdmin(val pagingData: PagingData<MemberListData>) : AdminViewState()
    data class SuccessRemoveMember(val message: String = ""): AdminViewState()
    data class SuccessPromoteMember(val message: String = ""): AdminViewState()
    data class SuccessDemoteAdmin(val message: String = ""): AdminViewState()
    data class SuccessTransferOwnership(val message: String = ""): AdminViewState()
    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : AdminViewState()
    data class InputError(val errorData: ErrorsData? = null) : AdminViewState()

}