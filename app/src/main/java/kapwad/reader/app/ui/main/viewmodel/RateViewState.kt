package kapwad.reader.app.ui.main.viewmodel

import kapwad.reader.app.data.model.ConsumerListModelData
import kapwad.reader.app.data.model.TempListModelData
import kapwad.reader.app.utils.PopupErrorState


sealed class TempViewState {
    object Idle : TempViewState()
    object Loading : TempViewState()

    data class SuccessDelete(val msg: String = "") : TempViewState()
    data class SuccessOfflineGetOrder(val data: List<TempListModelData>) : TempViewState()
    data class SuccessTotal(val total: String = "") : TempViewState()
    data class SuccessOfflineCreate(val data: List<TempListModelData>) : TempViewState()
    data class SuccessOrderList(val data: List<TempListModelData>) : TempViewState()

    data class SuccessOnlineTemp(
        val jsonData: String = "{}",
        val tempListModelData: List<TempListModelData> = emptyList()
    ) : TempViewState()


    data class PopupError(val errorCode: PopupErrorState, val message: String = "") :
        TempViewState()
}