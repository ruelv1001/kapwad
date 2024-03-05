package dswd.ziacare.app.ui.group.viewmodel

import androidx.paging.PagingData
import dswd.ziacare.app.data.model.ErrorsData
import dswd.ziacare.app.data.repositories.group.response.CreateGroupResponse
import dswd.ziacare.app.data.repositories.group.response.GroupData
import dswd.ziacare.app.data.repositories.wallet.response.BalanceData
import dswd.ziacare.app.data.repositories.wallet.response.GetBalanceResponse
import dswd.ziacare.app.data.repositories.wallet.response.QRData
import dswd.ziacare.app.data.repositories.wallet.response.SearchUserResponse
import dswd.ziacare.app.data.repositories.wallet.response.TransactionData
import dswd.ziacare.app.utils.PopupErrorState

sealed class GroupWalletViewState{

    object Loading : GroupWalletViewState()
    data class SuccessGetBalance(val balanceData: BalanceData? = null) : GroupWalletViewState()

    data class SuccessSendPoint(val data: TransactionData? = TransactionData()) : GroupWalletViewState()

    data class SuccessTransactionList(val pagingData: PagingData<TransactionData>) : GroupWalletViewState()

    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : GroupWalletViewState()

    data class InputError(val errorData: ErrorsData? = null) : GroupWalletViewState()

}
