package kapwad.reader.app.ui.phmarket.viewmodel

import kapwad.reader.app.data.model.ProductOrderListModelData
import kapwad.reader.app.utils.PopupErrorState


sealed class OrderViewState {
    object Idle : OrderViewState()
    object Loading : OrderViewState()

    data class SuccessDelete(val msg: String = "") : OrderViewState()
    data class SuccessOfflineGetOrder(val data: List<ProductOrderListModelData>) : OrderViewState()
    data class SuccessTotal(val total: String = "") : OrderViewState()
    data class SuccessOfflineCreateOrder(val data: ProductOrderListModelData) : OrderViewState()
    data class SuccessOrderList(val data: List<ProductOrderListModelData>) : OrderViewState()
    data class PopupError(val errorCode: PopupErrorState, val message: String = "") :
        OrderViewState()
}