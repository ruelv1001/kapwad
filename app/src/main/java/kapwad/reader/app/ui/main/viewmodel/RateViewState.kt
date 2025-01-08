package kapwad.reader.app.ui.main.viewmodel

import kapwad.reader.app.data.model.ConsumerListModelData
import kapwad.reader.app.data.model.RateAListModelData
import kapwad.reader.app.data.model.RateBListModelData
import kapwad.reader.app.data.model.RateCListModelData
import kapwad.reader.app.data.model.RateListModelData
import kapwad.reader.app.data.model.RateReListModelData
import kapwad.reader.app.data.model.TempListModelData

import kapwad.reader.app.utils.PopupErrorState


sealed class RateViewState {
    object Idle : RateViewState()
    object Loading : RateViewState()

    data class SuccessDelete(val msg: String = "") : RateViewState()
    data class SuccessOfflineGetOrder(val data: List<RateListModelData>) : RateViewState()
    data class SuccessOfflineGetOrderA(val data: List<RateAListModelData>) : RateViewState()
    data class SuccessOfflineGetOrderB(val data: List<RateBListModelData>) : RateViewState()
    data class SuccessOfflineGetOrderC(val data: List<RateCListModelData>) : RateViewState()
    data class SuccessOfflineGetOrderRe(val data: List<RateCListModelData>) : RateViewState()
    data class SuccessTotal(val total: String = "") : RateViewState()
    data class SuccessOfflineCreate(val data: List<RateListModelData>) : RateViewState()
    data class SuccessOfflineCreateA(val data: List<RateAListModelData>) : RateViewState()
    data class SuccessOfflineCreateB(val data: List<RateBListModelData>) : RateViewState()
    data class SuccessOfflineCreateC(val data: List<RateCListModelData>) : RateViewState()
    data class SuccessOfflineCreateRe(val data: List<RateReListModelData>) : RateViewState()
    data class SuccessOrderList(val data: List<RateListModelData>) : RateViewState()
    data class SuccessRateById(val data: RateListModelData?) : RateViewState()
    data class SuccessRateAById(val data: RateAListModelData?) : RateViewState()
    data class SuccessRateBById(val data: RateBListModelData?) : RateViewState()
    data class SuccessRateCById(val data: RateCListModelData?) : RateViewState()
    data class SuccessRateReById(val data: RateReListModelData?) : RateViewState()
    data class SuccessOnlineRate(
        val jsonData: String = "{}",
        val rateListModelData: List<RateListModelData> = emptyList()
    ) : RateViewState()

    data class SuccessOnlineRateA(
        val jsonData: String = "{}",
        val rateListModelData: List<RateAListModelData> = emptyList()
    ) : RateViewState()


    data class SuccessOnlineRateB(
        val jsonData: String = "{}",
        val rateListModelData: List<RateBListModelData> = emptyList()
    ) : RateViewState()


    data class SuccessOnlineRateC(
        val jsonData: String = "{}",
        val rateListModelData: List<RateCListModelData> = emptyList()
    ) : RateViewState()

    data class SuccessOnlineRateRe(
        val jsonData: String = "{}",
        val rateListModelData: List<RateReListModelData> = emptyList()
    ) : RateViewState()


    data class PopupError(val errorCode: PopupErrorState, val message: String = "") :
        RateViewState()
}