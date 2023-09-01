package com.lionscare.app.ui.wallet.viewmodel

import androidx.paging.PagingData
import com.lionscare.app.data.model.ErrorsData
import com.lionscare.app.data.repositories.group.response.CreateGroupResponse
import com.lionscare.app.data.repositories.wallet.response.BalanceData
import com.lionscare.app.data.repositories.wallet.response.GetBalanceResponse
import com.lionscare.app.data.repositories.wallet.response.QRData
import com.lionscare.app.data.repositories.wallet.response.SearchUserResponse
import com.lionscare.app.data.repositories.wallet.response.TransactionData
import com.lionscare.app.utils.PopupErrorState

sealed class WalletViewState{

    object Loading : WalletViewState()
    data class SuccessGetBalance(val balanceData: BalanceData? = null) : WalletViewState()

    data class SuccessTopup(val webUrl: String? = null) : WalletViewState()

    data class SuccessSendPoint(val data: TransactionData? = TransactionData()) : WalletViewState()

    data class SuccessScanQR(val scanQRData: QRData? = null) : WalletViewState()

    data class SuccessTransactionDetail(val details: TransactionData? = null) : WalletViewState()

    data class SuccessTransactionList(val pagingData: PagingData<TransactionData>) : WalletViewState()

    data class SuccessSearchUser(val listData: List<QRData>) : WalletViewState()

    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : WalletViewState()

    data class InputError(val errorData: ErrorsData? = null) : WalletViewState()

}
