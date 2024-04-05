package dswd.ziacare.app.ui.main.viewmodel

import dswd.ziacare.app.data.repositories.baseresponse.UserModel
import dswd.ziacare.app.utils.PopupErrorState

sealed class SettingsViewState{
    object Loading : SettingsViewState()
    object LoadingProfile : SettingsViewState()
    object LoadingBadge : SettingsViewState()
    data class Success(val message: String = "") : SettingsViewState()
    data class SuccessGetUserInfo(val message: String = "",val userModel: UserModel? = UserModel()) : SettingsViewState()
    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : SettingsViewState()
    data class InputError(val errorData: dswd.ziacare.app.data.model.ErrorsData? = null) : SettingsViewState()
}
