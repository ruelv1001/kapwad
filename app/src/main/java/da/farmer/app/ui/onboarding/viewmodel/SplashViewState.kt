package da.farmer.app.ui.onboarding.viewmodel

import da.farmer.app.utils.PopupErrorState


sealed class SplashViewState {
    object Idle : SplashViewState()
    object Loading : SplashViewState()
    data class SuccessRefreshToken(val status: Boolean = false, val is_complete_profile: Boolean = false) : SplashViewState()
    data class PopupError(val errorCode: PopupErrorState, val message: String = "") :
        SplashViewState()
}