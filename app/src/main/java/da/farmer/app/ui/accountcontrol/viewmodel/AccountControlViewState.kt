package da.farmer.app.ui.accountcontrol.viewmodel

import da.farmer.app.data.model.ErrorsData
import da.farmer.app.data.repositories.auth.response.ReasonsResponse
import da.farmer.app.data.repositories.baseresponse.GeneralResponse
import da.farmer.app.utils.PopupErrorState


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
