package dswd.ziacare.app.ui.accountcontrol.viewmodel

import dswd.ziacare.app.data.model.ErrorsData
import dswd.ziacare.app.data.repositories.auth.response.ReasonsResponse
import dswd.ziacare.app.data.repositories.baseresponse.GeneralResponse
import dswd.ziacare.app.utils.PopupErrorState


sealed class AccountControlViewState{
    object Loading : AccountControlViewState()
    object SuccessClearLocalData : AccountControlViewState()

    data class SuccessDeleteOrDeactivateAccount(val response: GeneralResponse): AccountControlViewState()
    data class SuccessDeleteOrDeactivateAccountOTP(val response: GeneralResponse): AccountControlViewState()
    data class SuccessGetReasons(val response: ReasonsResponse): AccountControlViewState()
    data class PopupError(
        val errorCode: PopupErrorState,
        val message: String = "",
        val badge: String = ""
    ) : AccountControlViewState()

    data class InputError(val errorData: ErrorsData? = null) : AccountControlViewState()
}
