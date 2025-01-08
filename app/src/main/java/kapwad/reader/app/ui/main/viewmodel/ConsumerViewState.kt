package kapwad.reader.app.ui.main.viewmodel

import kapwad.reader.app.data.model.ConsumerListModelData
import kapwad.reader.app.utils.PopupErrorState


sealed class ConsumerViewState {
    object Idle : ConsumerViewState()
    object Loading : ConsumerViewState()

    data class SuccessDelete(val msg: String = "") : ConsumerViewState()
    data class SuccessOfflineGetOrder(val data: List<ConsumerListModelData>) : ConsumerViewState()

    data class SuccessOfflineGetSearch(val data: List<ConsumerListModelData>) : ConsumerViewState()
    data class SuccessTotal(val total: String = "") : ConsumerViewState()
    data class SuccessOfflineCreateOrder(val data: List<ConsumerListModelData>) : ConsumerViewState()
    data class SuccessOrderList(val data: List<ConsumerListModelData>) : ConsumerViewState()
    data class SuccessConsumerById(val data: ConsumerListModelData?) : ConsumerViewState()
    data class SuccessOnlineConsumer(
        val jsonData: String = "{}",
        val consumerListModelData: List<ConsumerListModelData> = emptyList()
    ) : ConsumerViewState()
    data class PopupError(val errorCode: PopupErrorState, val message: String = "") :
        ConsumerViewState()
}