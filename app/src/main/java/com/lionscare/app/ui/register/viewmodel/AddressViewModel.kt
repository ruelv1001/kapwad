package com.lionscare.app.ui.register.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope


import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lionscare.app.data.model.ErrorModel
import com.lionscare.app.data.repositories.address.AddressRepository
import com.lionscare.app.data.repositories.address.request.LocationRequest
import com.lionscare.app.data.repositories.address.request.MunicipalityListRequest
import com.lionscare.app.data.repositories.address.request.RegistrationRequest
import com.lionscare.app.data.repositories.address.request.ZoneRequest
import com.lionscare.app.utils.AppConstant
import com.lionscare.app.utils.PopupErrorState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeoutException
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val addressRepository: AddressRepository
) : ViewModel() {

    private val _addressSharedFlow = MutableSharedFlow<AddressViewState>()

    val addressSharedFlow: SharedFlow<AddressViewState> =
        _addressSharedFlow.asSharedFlow()

    var provinceSku = ""
    var citySku = ""
    var crgySku = ""
    var provinceCode = ""
    var cityCode = ""
    var brgyCode = ""
    var provinceName = ""
    var cityName = ""
    var brgyName = ""
    var reference = 0
    var zipCode = ""

    var resendEnable = false

    fun getCountryList(displayAllowedCountries: Boolean = false) {
        viewModelScope.launch {
            addressRepository.getCountryList(displayAllowedCountries)
                .onStart {
                    _addressSharedFlow.emit(AddressViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)

                }
                .collect {
                    _addressSharedFlow.emit(
                        AddressViewState.SuccessGetProvinceList(it.data.orEmpty())
                    )
                }
        }
    }

    fun getProvinceList() {
        viewModelScope.launch {
            addressRepository.getProvinceList()
                .onStart {
                    _addressSharedFlow.emit(AddressViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)

                }
                .collect {
                    _addressSharedFlow.emit(
                        AddressViewState.SuccessGetProvinceList(it.data.orEmpty())
                    )
                }
        }
    }



    fun getMunicipalityList(reference: String) {
        viewModelScope.launch {
            val municipalityListRequest = MunicipalityListRequest(
                reference = reference,
                type = "id"
            )
            addressRepository.getMunicipalityList(municipalityListRequest)
                .onStart {
                    _addressSharedFlow.emit(AddressViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)

                }
                .collect {
                    _addressSharedFlow.emit(
                        AddressViewState.SuccessGetMunicipalityList(it.data.orEmpty())
                    )
                }
        }
    }

    fun getBrgyList(reference: String) {
        viewModelScope.launch {
            val municipalityListRequest = MunicipalityListRequest(
                reference = reference,
                type = "id"
            )
            addressRepository.getBarangay(municipalityListRequest)
                .onStart {
                    _addressSharedFlow.emit(AddressViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)

                }
                .collect {
                    _addressSharedFlow.emit(
                        AddressViewState.SuccessGetMunicipalityList(it.data.orEmpty())
                    )
                }
        }

    }


    //==================================LionsClub
    fun getRegionList() {
        viewModelScope.launch {
            addressRepository.getRegionList()
                .onStart {
                    _addressSharedFlow.emit(AddressViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)

                }
                .collect {
                    _addressSharedFlow.emit(
                        AddressViewState.SuccessGetLionsClubLOV(it.data.orEmpty())
                    )
                }
        }
    }

    fun getZoneList(request: ZoneRequest) {
        viewModelScope.launch {
            addressRepository.getZoneList(request)
                .onStart {
                    _addressSharedFlow.emit(AddressViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)

                }
                .collect {
                    _addressSharedFlow.emit(
                        AddressViewState.SuccessGetLionsClubLOV(it.data.orEmpty())
                    )
                }
        }
    }

    fun getLocationList(request: LocationRequest) {
        viewModelScope.launch {
            addressRepository.getLocationList(request)
                .onStart {
                    _addressSharedFlow.emit(AddressViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)

                }
                .collect {
                    _addressSharedFlow.emit(
                        AddressViewState.SuccessGetLionsClubLOV(it.data.orEmpty())
                    )
                }
        }
    }

    private suspend fun onError(exception: Throwable) {
        when (exception) {
            is IOException,
            is TimeoutException,
            -> {
                _addressSharedFlow.emit(
                    AddressViewState.PopupError(
                        PopupErrorState.NetworkError
                    )
                )
            }
            is HttpException -> {
                val errorBody = exception.response()?.errorBody()
                val gson = Gson()
                val type = object : TypeToken<ErrorModel>() {}.type
                var errorResponse: ErrorModel? = gson.fromJson(errorBody?.charStream(), type)
                if (errorResponse?.has_requirements == true) {
                    _addressSharedFlow.emit(AddressViewState.InputError(errorResponse.errors))
                } else {
                    _addressSharedFlow.emit(
                        AddressViewState.PopupError(
                            if (AppConstant.isSessionStatusCode(errorResponse?.status_code.orEmpty())){
                                PopupErrorState.SessionError
                            }else{
                                PopupErrorState.HttpError
                            }
                            , errorResponse?.msg.orEmpty()
                        )
                    )
                }

            }
            else -> _addressSharedFlow.emit(
                AddressViewState.PopupError(
                    PopupErrorState.UnknownError
                )
            )
        }
    }

}
