package kapwad.reader.app.ui.main.viewmodel

import kapwad.reader.app.data.repositories.baseresponse.UserModel
import kapwad.reader.app.utils.PopupErrorState

sealed class SettingsViewState{
    object Loading : SettingsViewState()
    object LoadingProfile : SettingsViewState()
    object LoadingBadge : SettingsViewState()
    data class Success(val message: String = "") : SettingsViewState()
    data class SuccessGetUserInfo(val message: String = "",val userModel: UserModel? = UserModel()) : SettingsViewState()
    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : SettingsViewState()
    data class InputError(val errorData: kapwad.reader.app.data.model.ErrorsData? = null) : SettingsViewState()
}
