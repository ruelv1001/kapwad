package com.ziacare.app.ui.bulletin.viewmodel

import androidx.paging.PagingData
import com.ziacare.app.data.repositories.billing.response.BillData
import com.ziacare.app.utils.PopupErrorState

sealed class BillViewState{
    object Loading : BillViewState()
    data class SuccessGetAllBillList(val pagingData: PagingData<BillData>) : BillViewState()
    data class SuccessGetAskForDonationList(val pagingData: PagingData<BillData>) : BillViewState()
    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : BillViewState()
}

