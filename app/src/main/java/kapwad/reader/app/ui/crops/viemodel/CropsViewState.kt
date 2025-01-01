package kapwad.reader.app.ui.crops.viemodel

import androidx.paging.PagingData
import kapwad.reader.app.data.model.ErrorsData
import kapwad.reader.app.data.repositories.crops.response.CropDetailsResponse
import kapwad.reader.app.data.repositories.crops.response.CropItemListResponse
import kapwad.reader.app.data.repositories.crops.response.CropsData
import kapwad.reader.app.data.repositories.crops.response.CropsResponse
import kapwad.reader.app.utils.PopupErrorState

sealed class CropsViewState{

    object Loading : CropsViewState()
    object LoadingScanGroup : CropsViewState()
    object LoadingScan : CropsViewState()

    data class SuccessCropDetails(val message: String = "",val cropDetailsResponse: CropDetailsResponse) : CropsViewState()
    data class SuccessCropsList(val pagingData: PagingData<CropsData>) : CropsViewState()
    data class SuccessStart(val message : String = "") : CropsViewState()
    data class SuccessUploadVideo(val message : String = "") : CropsViewState()
    data class SuccessUploadImage(val message : String = "") : CropsViewState()
    data class Success(
        val message: String = "",
        val cropsResponse: CropsResponse? = CropsResponse()
    ) : CropsViewState()

    data class SuccessCropItem(
        val message: String = "",
        val cropItemListResponse: CropItemListResponse? = CropItemListResponse()
    ) : CropsViewState()

    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : CropsViewState()

    data class InputError(val errorData: ErrorsData? = null) : CropsViewState()

}
