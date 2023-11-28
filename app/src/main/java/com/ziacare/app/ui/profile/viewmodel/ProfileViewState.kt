package com.ziacare.app.ui.profile.viewmodel

import androidx.paging.PagingData
import com.ziacare.app.data.model.ErrorsData
import com.ziacare.app.data.repositories.baseresponse.GeneralResponse
import com.ziacare.app.data.repositories.baseresponse.UserModel
import com.ziacare.app.data.repositories.profile.response.LOVResponse
import com.ziacare.app.data.repositories.profile.response.ProfileVerificationResponse
import com.ziacare.app.data.repositories.profile.response.UserNotificationData
import com.ziacare.app.utils.PopupErrorState

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
