package com.lionscare.app.ui.sample.viewmodel

import com.lionscare.app.utils.PopupErrorState


sealed class SplashViewState {
    object Idle : SplashViewState()
    object Loading : SplashViewState()
    data class SuccessRefreshToken(val status: Boolean = false) : SplashViewState()
    data class PopupError(val errorCode: PopupErrorState, val message: String = "") :
        SplashViewState()
}