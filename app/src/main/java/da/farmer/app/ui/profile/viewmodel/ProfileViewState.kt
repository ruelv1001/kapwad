package da.farmer.app.ui.profile.viewmodel

import androidx.paging.PagingData
import da.farmer.app.data.model.ErrorsData
import da.farmer.app.data.repositories.baseresponse.GeneralResponse
import da.farmer.app.data.repositories.baseresponse.UserModel
import da.farmer.app.data.repositories.profile.response.LOVResponse
import da.farmer.app.data.repositories.profile.response.ProfileVerificationResponse
import da.farmer.app.data.repositories.profile.response.UserNotificationData
import da.farmer.app.utils.PopupErrorState

sealed class ProfileViewState {
    object Loading : ProfileViewState()
    object LoadingBadgeStatus : ProfileViewState()
    object LoadingAvatar : ProfileViewState()
    object LoadingVerificationStatus : ProfileViewState()
    data class Success(val message: String = "") : ProfileViewState()
    data class SuccessGetUserInfo(val message: String = "",val userModel: UserModel? = UserModel()) : ProfileViewState()

    //KYC
    data class SuccessLoadLOVIds(val message : String = "", val lovResponse: LOVResponse? = LOVResponse()) : ProfileViewState()
    data class SuccessLoadLOVProofOfAddress(val message : String = "", val lovResponse: LOVResponse? = LOVResponse()) : ProfileViewState()
    data class SuccessUploadId(val message : String = "") : ProfileViewState()
    data class SuccessUploadAddress(val message : String = "") : ProfileViewState()
    data class SuccessUploadFacialId(val message : String = "") : ProfileViewState()


    data class SuccessUploadAvatar(val message : String = "") : ProfileViewState()

    data class SuccessGetVerificationStatus(val message : String = "", val profileVerificationResponse: ProfileVerificationResponse) : ProfileViewState()

    //PHONE NUMBER
    data class SuccessUpdatePhoneNumber(val response: GeneralResponse) : ProfileViewState()
    data class SuccessUpdatePhoneNumberWithOTP(val response: GeneralResponse) : ProfileViewState()

    data class SuccessGetNotificationList(val pagingData: PagingData<UserNotificationData>):ProfileViewState()
    data class PopupError(val errorCode: PopupErrorState, val message: String = "", val badge : String = "") : ProfileViewState()
    data class InputError(val errorData: ErrorsData? = null) : ProfileViewState()
}
