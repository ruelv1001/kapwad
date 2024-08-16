package da.farmer.app.ui.group.viewmodel

import androidx.paging.PagingData
import da.farmer.app.data.model.ErrorsData
import da.farmer.app.data.repositories.group.response.CreateGroupResponse
import da.farmer.app.data.repositories.group.response.GroupData
import da.farmer.app.data.repositories.wallet.response.BalanceData
import da.farmer.app.data.repositories.wallet.response.GetBalanceResponse
import da.farmer.app.data.repositories.wallet.response.QRData
import da.farmer.app.data.repositories.wallet.response.SearchUserResponse
import da.farmer.app.data.repositories.wallet.response.TransactionData
import da.farmer.app.utils.PopupErrorState

sealed class GroupWalletViewState{

    object Loading : GroupWalletViewState()
    data class SuccessGetBalance(val balanceData: BalanceData? = null) : GroupWalletViewState()

    data class SuccessSendPoint(val data: TransactionData? = TransactionData()) : GroupWalletViewState()

    data class SuccessTransactionList(val pagingData: PagingData<TransactionData>) : GroupWalletViewState()

    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : GroupWalletViewState()

    data class InputError(val errorData: ErrorsData? = null) : GroupWalletViewState()

}
