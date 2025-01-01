package kapwad.reader.app.ui.geotagging.viewmodel

import kapwad.reader.app.data.model.ErrorsData
import kapwad.reader.app.data.repositories.crops.response.CropItemListResponse
import kapwad.reader.app.data.repositories.crops.response.CropsResponse
import kapwad.reader.app.data.repositories.geotagging.response.GeoTaggingResponse
import kapwad.reader.app.utils.PopupErrorState

sealed class GeoTaggingViewState{

    object Loading : GeoTaggingViewState()
    object LoadingScanGroup : GeoTaggingViewState()
    object LoadingScan : GeoTaggingViewState()

    data class SuccessStatus(val message: String = "",val geoTaggingResponse: GeoTaggingResponse) : GeoTaggingViewState()

    data class SuccessUploadImage(val message : String = "") : GeoTaggingViewState()
    data class Success(
        val message: String = "",
        val cropsResponse: CropsResponse? = CropsResponse()
    ) : GeoTaggingViewState()

    data class SuccessCropItem(
        val message: String = "",
        val cropItemListResponse: CropItemListResponse? = CropItemListResponse()
    ) : GeoTaggingViewState()

    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : GeoTaggingViewState()

    data class InputError(val errorData: ErrorsData? = null) : GeoTaggingViewState()

}
