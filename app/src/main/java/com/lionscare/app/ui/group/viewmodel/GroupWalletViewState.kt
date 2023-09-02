package com.lionscare.app.ui.group.viewmodel

import androidx.paging.PagingData
import com.lionscare.app.data.model.ErrorsData
import com.lionscare.app.data.repositories.group.response.CreateGroupResponse
import com.lionscare.app.data.repositories.group.response.GroupData
import com.lionscare.app.data.repositories.wallet.response.BalanceData
import com.lionscare.app.data.repositories.wallet.response.GetBalanceResponse
import com.lionscare.app.data.repositories.wallet.response.QRData
import com.lionscare.app.data.repositories.wallet.response.SearchUserResponse
import com.lionscare.app.data.repositories.wallet.response.TransactionData
import com.lionscare.app.utils.PopupErrorState

sealed class GroupWalletViewState{

    object Loading : GroupWalletViewState()
    data class SuccessGetBalance(val balanceData: BalanceData? = null) : GroupWalletViewState()

    data class SuccessSendPoint(val data: TransactionData? = TransactionData()) : GroupWalletViewState()

    data class SuccessTransactionList(val pagingData: PagingData<TransactionData>) : GroupWalletViewState()

    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : GroupWalletViewState()

    data class InputError(val errorData: ErrorsData? = null) : GroupWalletViewState()

}
