package com.ziacare.app.ui.group.viewmodel

import androidx.paging.PagingData
import com.ziacare.app.data.model.ErrorsData
import com.ziacare.app.data.repositories.group.response.CreateGroupResponse
import com.ziacare.app.data.repositories.group.response.GroupData
import com.ziacare.app.data.repositories.wallet.response.BalanceData
import com.ziacare.app.data.repositories.wallet.response.GetBalanceResponse
import com.ziacare.app.data.repositories.wallet.response.QRData
import com.ziacare.app.data.repositories.wallet.response.SearchUserResponse
import com.ziacare.app.data.repositories.wallet.response.TransactionData
import com.ziacare.app.utils.PopupErrorState

sealed class GroupWalletViewState{

    object Loading : GroupWalletViewState()
    data class SuccessGetBalance(val balanceData: BalanceData? = null) : GroupWalletViewState()

    data class SuccessSendPoint(val data: TransactionData? = TransactionData()) : GroupWalletViewState()

    data class SuccessTransactionList(val pagingData: PagingData<TransactionData>) : GroupWalletViewState()

    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : GroupWalletViewState()

    data class InputError(val errorData: ErrorsData? = null) : GroupWalletViewState()

}
