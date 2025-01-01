package kapwad.reader.app.ui.main.viewmodel

import kapwad.reader.app.data.model.ConsumerListModelData
import kapwad.reader.app.data.model.OtherListModelData

import kapwad.reader.app.utils.PopupErrorState


sealed class OthersViewState {
    object Idle : OthersViewState()
    object Loading : OthersViewState()

    data class SuccessDelete(val msg: String = "") : OthersViewState()
    data class SuccessOfflineGetOrder(val data: List<OtherListModelData>) : OthersViewState()
    data class SuccessTotal(val total: String = "") : OthersViewState()
    data class SuccessOfflineCreate(val data: List<OtherListModelData>) : OthersViewState()
    data class SuccessOrderList(val data: List<OtherListModelData>) : OthersViewState()
    data class SuccessOtherById(val data: OtherListModelData?) : OthersViewState()
    data class SuccessOnlineOther(
        val jsonData: String = "{}",
        val otherListModelData: List<OtherListModelData> = emptyList()
    ) : OthersViewState()


    data class PopupError(val errorCode: PopupErrorState, val message: String = "") :
        OthersViewState()
}