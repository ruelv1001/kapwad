package com.lionscare.app.ui.billing.viewstate

import androidx.paging.PagingData
import com.lionscare.app.data.model.ErrorsData
import com.lionscare.app.data.repositories.group.response.GroupListData
import com.lionscare.app.data.repositories.group.response.ImmediateFamilyResponse
import com.lionscare.app.ui.main.viewmodel.GroupListViewState
import com.lionscare.app.ui.main.viewmodel.ImmediateFamilyViewState
import com.lionscare.app.utils.PopupErrorState

sealed class BillingViewState{
    object LoadingBillingDetails : BillingViewState()
    object LoadingMembers: BillingViewState()
    object LoadingGroups : BillingViewState()
    object LoadingFamily : BillingViewState()
    object LoadingGroupsRequestedForDonations : BillingViewState()
    data class SuccessGroupsRequestedForDonations(val pagingData: PagingData<GroupListData>) : BillingViewState()
    data class SuccessLoadBillingDetails(val id: String) : BillingViewState()
    data class SuccessLoadGroup(val pagingData: PagingData<GroupListData>) : BillingViewState()
    data class SuccessLoadFamily(val immediateFamilyResponse: ImmediateFamilyResponse? = null) : BillingViewState()

    data class PopupError(val errorCode: PopupErrorState, val message: String = "", val endpoint: String = "") : BillingViewState()
    data class InputError(val errorData: ErrorsData? = null) : BillingViewState()
}
