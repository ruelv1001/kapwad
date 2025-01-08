package kapwad.reader.app.ui.main.viewmodel

import kapwad.reader.app.data.model.ConsumerListModelData
import kapwad.reader.app.data.model.CreatedBillListModelData
import kapwad.reader.app.data.model.MeterReaderListModelData
import kapwad.reader.app.data.model.ProductOrderListModelData
import kapwad.reader.app.data.repositories.bill.response.BillUploadedResponse
import kapwad.reader.app.data.repositories.crops.response.CropDetailsResponse
import kapwad.reader.app.ui.crops.viemodel.CropsViewState
import kapwad.reader.app.utils.PopupErrorState


sealed class BillingViewState {
    object Idle : BillingViewState()
    object Loading : BillingViewState()

    data class SuccessDelete(val msg: String = "") : BillingViewState()
    data class SuccessOfflineGetOrder(val data: List<CreatedBillListModelData>) : BillingViewState()
    data class SuccessUpload(val billUploadedResponse: BillUploadedResponse) : BillingViewState()
    data class SuccessTotal(val total: String = "") : BillingViewState()
    data class SuccessOfflineCreateOrder(val data: CreatedBillListModelData) : BillingViewState()
    data class SuccessOrderList(val data: List<CreatedBillListModelData>) : BillingViewState()
    data class SuccessOfflineGetSearch(val data: List<CreatedBillListModelData>) : BillingViewState()
    data class PopupError(val errorCode: PopupErrorState, val message: String = "") :
        BillingViewState()
    data class SuccessExisted(val data: CreatedBillListModelData?) : BillingViewState()
    data class Success(
        val successCount: Int,
        val message: String
    ) : BillingViewState()
    data class Error(
        val errorMessage: String
    ) : BillingViewState()
}