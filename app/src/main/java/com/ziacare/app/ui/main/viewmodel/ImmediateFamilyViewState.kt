package com.ziacare.app.ui.main.viewmodel


import com.ziacare.app.data.repositories.group.response.ImmediateFamilyResponse
import com.ziacare.app.utils.PopupErrorState

sealed class ImmediateFamilyViewState {

    object Loading : ImmediateFamilyViewState()
    data class Success(val immediateFamilyResponse: ImmediateFamilyResponse? = null) : ImmediateFamilyViewState()
    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : ImmediateFamilyViewState()

}