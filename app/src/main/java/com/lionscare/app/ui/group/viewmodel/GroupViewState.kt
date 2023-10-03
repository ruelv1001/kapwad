package com.lionscare.app.ui.group.viewmodel

import com.lionscare.app.data.model.ErrorsData
import com.lionscare.app.data.repositories.group.response.CreateGroupResponse
import com.lionscare.app.data.repositories.group.response.GroupData
import com.lionscare.app.ui.profile.viewmodel.ProfileViewState
import com.lionscare.app.ui.wallet.viewmodel.WalletViewState
import com.lionscare.app.utils.PopupErrorState

sealed class GroupViewState{

    object Loading : GroupViewState()
    data class SuccessCreateGroup(val createGroupResponse: CreateGroupResponse? = null) : GroupViewState()
    data class SuccessUpdateGroup(val createGroupResponse: CreateGroupResponse? = null) : GroupViewState()
    data class SuccessShowGroup(val createGroupResponse: CreateGroupResponse? = null) : GroupViewState()
    data class SuccessUploadAvatar(val message : String = "") : GroupViewState()

    data class SuccessSearchGroup(val listData: List<GroupData>) : GroupViewState()
    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : GroupViewState()
    data class InputError(val errorData: ErrorsData? = null) : GroupViewState()

}
