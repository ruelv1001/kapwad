package com.ziacare.app.ui.wallet.viewmodel

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

sealed class WalletViewState{

    object Loading : WalletViewState()
    object LoadingScanGroup : WalletViewState()
    object LoadingScan : WalletViewState()
    data class SuccessGetBalance(val balanceData: BalanceData? = null) : WalletViewState()

    data class SuccessTopup(val webUrl: String? = null) : WalletViewState()
    data class SuccessScan2Pay(val message: String? = null) : WalletViewState()

    data class SuccessSendPoint(val data: TransactionData? = TransactionData()) : WalletViewState()

    data class SuccessScanQR(val scanQRData: QRData? = null) : WalletViewState()

    data class SuccessTransactionDetail(val details: TransactionData? = null) : WalletViewState()

    data class SuccessTransactionList(val pagingData: PagingData<TransactionData>) : WalletViewState()

    data class SuccessSearchUser(val listData: List<QRData>) : WalletViewState()

    data class SuccessSearchGroup(val listData: List<GroupData>) : WalletViewState()
    data class SuccessScanGroup(val groupData: GroupData) : WalletViewState()

    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : WalletViewState()

    data class InputError(val errorData: ErrorsData? = null) : WalletViewState()

}
