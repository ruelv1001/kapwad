package com.lionscare.app.ui.profile.viewmodel

import com.lionscare.app.data.model.ErrorsData
import com.lionscare.app.data.repositories.baseresponse.GeneralResponse
import com.lionscare.app.data.repositories.baseresponse.UserModel
import com.lionscare.app.data.repositories.profile.response.BadgeRemovalStatus
import com.lionscare.app.data.repositories.profile.response.BadgeResponse
import com.lionscare.app.data.repositories.profile.response.BadgeStatus
import com.lionscare.app.data.repositories.profile.response.BadgeStatusResponse
import com.lionscare.app.data.repositories.profile.response.LOVResponse
import com.lionscare.app.data.repositories.profile.response.ProfileVerificationResponse
import com.lionscare.app.utils.PopupErrorState

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

    //BADGE
    data class SuccessGetBadgeStatus(val message : String = "", val badgeStatusResponse: BadgeStatusResponse) : ProfileViewState()
    data class SuccessBadgeRequest(val message : String = "", val badgeResponse: BadgeResponse) : ProfileViewState()
    data class SuccessRequestBadgeRemoval(val message : String = "") : ProfileViewState()
    data class SuccessBadgeRemovalStatus(val badgeRemovalStatus: BadgeRemovalStatus? = BadgeRemovalStatus()) : ProfileViewState()


    //PHONE NUMBER
    data class SuccessUpdatePhoneNumber(val response: GeneralResponse) : ProfileViewState()
    data class SuccessUpdatePhoneNumberWithOTP(val response: GeneralResponse) : ProfileViewState()
    data class PopupError(val errorCode: PopupErrorState, val message: String = "", val badge : String = "") : ProfileViewState()
    data class InputError(val errorData: ErrorsData? = null) : ProfileViewState()
}
