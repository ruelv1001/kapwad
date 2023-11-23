package com.ziacare.app.ui.billing.viewstate

import androidx.paging.PagingData
import com.ziacare.app.data.model.ErrorsData
import com.ziacare.app.data.repositories.billing.response.BillData
import com.ziacare.app.data.repositories.billing.response.BillDetailsData
import com.ziacare.app.data.repositories.group.response.GroupListData
import com.ziacare.app.data.repositories.group.response.ImmediateFamilyResponse
import com.ziacare.app.utils.PopupErrorState

sealed class BillingViewState{
    object LoadingBillingDetails : BillingViewState()
    object LoadingMembers: BillingViewState()
    object LoadingGroups : BillingViewState()
    object LoadingFamily : BillingViewState()
    object LoadingMyBills : BillingViewState()
    object LoadingGroupsRequestedForDonations : BillingViewState()
    data class SuccessGroupsRequestedForDonations(val pagingData: PagingData<GroupListData>) : BillingViewState()
    data class SuccessLoadBillingDetails(val data: BillDetailsData? = null) : BillingViewState()
    data class SuccessLoadGroup(val pagingData: PagingData<GroupListData>) : BillingViewState()
    data class SuccessLoadFamily(val immediateFamilyResponse: ImmediateFamilyResponse? = null) : BillingViewState()
    data class SuccessMyListOfBills(val pagingData: PagingData<BillData>) : BillingViewState()
    data class PopupError(val errorCode: PopupErrorState, val message: String = "", val endpoint: String = "") : BillingViewState()
    data class InputError(val errorData: ErrorsData? = null) : BillingViewState()
}
