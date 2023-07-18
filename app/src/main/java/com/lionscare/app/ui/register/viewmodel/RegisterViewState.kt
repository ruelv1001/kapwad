package com.lionscare.app.ui.register.viewmodel

import com.lionscare.app.data.model.ErrorsData
import com.lionscare.app.utils.PopupErrorState

sealed class RegisterViewState{
    object Loading : RegisterViewState()
    data class Success(val message: String = "") : RegisterViewState()
    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : RegisterViewState()
    data class InputError(val errorData: ErrorsData? = null) : RegisterViewState()
}
