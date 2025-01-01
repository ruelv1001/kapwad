package kapwad.reader.app.ui.group.viewmodel

import androidx.paging.PagingData
import kapwad.reader.app.data.model.ErrorsData
import kapwad.reader.app.data.repositories.wallet.response.BalanceData
import kapwad.reader.app.data.repositories.wallet.response.TransactionData
import kapwad.reader.app.utils.PopupErrorState

sealed class GroupWalletViewState{

    object Loading : GroupWalletViewState()
    data class SuccessGetBalance(val balanceData: BalanceData? = null) : GroupWalletViewState()

    data class SuccessSendPoint(val data: TransactionData? = TransactionData()) : GroupWalletViewState()

    data class SuccessTransactionList(val pagingData: PagingData<TransactionData>) : GroupWalletViewState()

    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : GroupWalletViewState()

    data class InputError(val errorData: ErrorsData? = null) : GroupWalletViewState()

}
