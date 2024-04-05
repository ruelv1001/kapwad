package dswd.ziacare.app.ui.accountcontrol.viewmodel

import dswd.ziacare.app.data.model.ErrorsData
import dswd.ziacare.app.utils.PopupErrorState


sealed class AccountControlViewState{
    object Loading : AccountControlViewState()
    object SuccessClearLocalData : AccountControlViewState()
    data class PopupError(
        val errorCode: PopupErrorState,
        val message: String = "",
        val badge: String = ""
    ) : AccountControlViewState()

    data class InputError(val errorData: ErrorsData? = null) : AccountControlViewState()
}
