package kapwad.reader.app.ui.main.viewmodel

import kapwad.reader.app.data.model.CreatedBillListModelData
import kapwad.reader.app.data.model.ProductOrderListModelData
import kapwad.reader.app.utils.PopupErrorState


sealed class BillingViewState {
    object Idle : BillingViewState()
    object Loading : BillingViewState()

    data class SuccessDelete(val msg: String = "") : BillingViewState()
    data class SuccessOfflineGetOrder(val data: List<CreatedBillListModelData>) : BillingViewState()
    data class SuccessTotal(val total: String = "") : BillingViewState()
    data class SuccessOfflineCreateOrder(val data: CreatedBillListModelData) : BillingViewState()
    data class SuccessOrderList(val data: List<CreatedBillListModelData>) : BillingViewState()
    data class PopupError(val errorCode: PopupErrorState, val message: String = "") :
        BillingViewState()
}