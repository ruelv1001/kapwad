package com.lionscare.app.ui.register.viewmodel

import com.lionscare.app.data.model.ErrorsData
import com.lionscare.app.data.repositories.address.response.AddressData
import com.lionscare.app.utils.PopupErrorState

sealed class AddressViewState{
    object Loading : AddressViewState()
    object Idle : AddressViewState()
    object LoadingRegister : AddressViewState()
    data class SuccessRegister(val message: String = "", val status: Boolean = false) : AddressViewState()
    data class Success(val message: String = "") : AddressViewState()
    data class SuccessPreReg(val message: String = "") : AddressViewState()
    data class SuccessPhoneValidate(val message: String = "") : AddressViewState()
    data class SuccessReg(val message: String = "") : AddressViewState()
    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : AddressViewState()
    data class SuccessGetProvinceList(val provinceList: List<AddressData> = emptyList()) : AddressViewState()
    data class SuccessGetMunicipalityList(val provinceList: List<AddressData> = emptyList()) : AddressViewState()
    data class InputError(val errorData: ErrorsData? = null) : AddressViewState()
}
