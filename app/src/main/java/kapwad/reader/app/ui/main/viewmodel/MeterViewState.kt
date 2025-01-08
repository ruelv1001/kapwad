package kapwad.reader.app.ui.main.viewmodel

import kapwad.reader.app.data.model.ConsumerListModelData
import kapwad.reader.app.data.model.MeterReaderListModelData


import kapwad.reader.app.utils.PopupErrorState


sealed class MeterViewState {
    object Idle : MeterViewState()
    object Loading : MeterViewState()

    data class SuccessDelete(val msg: String = "") : MeterViewState()
    data class SuccessOfflineGetOrder(val data: List<MeterReaderListModelData>) : MeterViewState()
    data class SuccessTotal(val total: String = "") : MeterViewState()
    data class SuccessOfflineCreate(val data: List<MeterReaderListModelData>) : MeterViewState()
    data class SuccessMeterList(val data: List<MeterReaderListModelData>) : MeterViewState()
    data class SuccessOtherById(val data: MeterReaderListModelData?) : MeterViewState()
    data class Error(val message: String) : MeterViewState()
    data class SuccessMeterByAccount(val data: MeterReaderListModelData?) : MeterViewState()
    data class SuccessOnlineMeter(
        val jsonData: String = "{}",
        val meterReaderListModelData: List<MeterReaderListModelData> = emptyList()
    ) : MeterViewState()


    data class PopupError(val errorCode: PopupErrorState, val message: String = "") :
        MeterViewState()
}