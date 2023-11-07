package com.lionscare.app.ui.billing.viewstate

import com.lionscare.app.data.model.ErrorsData
import com.lionscare.app.utils.PopupErrorState

sealed class BillingViewState{
    object Loading : BillingViewState()
    data class SuccessLoadBillingDetails(val id: String) : BillingViewState()
    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : BillingViewState()
    data class InputError(val errorData: ErrorsData? = null) : BillingViewState()
}
