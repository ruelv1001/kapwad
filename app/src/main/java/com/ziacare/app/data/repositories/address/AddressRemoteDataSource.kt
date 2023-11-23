package com.ziacare.app.data.repositories.address

import com.ziacare.app.data.repositories.address.request.CountryListRequest
import com.ziacare.app.data.repositories.address.request.LocationRequest
import com.ziacare.app.data.repositories.address.request.MunicipalityListRequest
import com.ziacare.app.data.repositories.address.request.RegionRequest
import com.ziacare.app.data.repositories.address.request.ZoneRequest
import com.ziacare.app.data.repositories.address.response.AddressResponse
import com.ziacare.app.data.repositories.profile.response.LOVResponse
import retrofit2.HttpException
import java.net.HttpURLConnection
import javax.inject.Inject

class AddressRemoteDataSource @Inject constructor(private val addressService: AddressService) {

    suspend fun getCountryList(displayAllowedCountries: Boolean): AddressResponse {
        val request = CountryListRequest(displayAllowedCountries)
        val response = addressService.doGetCountryList(request = request)
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun getProvinceList(): AddressResponse {
        val response = addressService.doGetProvinceList()
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun getMunicipalityList(municipalityListRequest: MunicipalityListRequest): AddressResponse {
        val response =
            addressService.doGetMunicipalityList(municipalityListRequest = municipalityListRequest)
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun getBarangayList(municipalityListRequest: MunicipalityListRequest): AddressResponse {
        val response =
            addressService.doGetBarangayList(municipalityListRequest = municipalityListRequest)
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun getRegionList(): LOVResponse {
        val response = addressService.getRegionList(RegionRequest())
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }


    suspend fun getZoneList(request : ZoneRequest): LOVResponse {
        val response = addressService.getZoneList(request)
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }


    suspend fun getLocationList(request : LocationRequest): LOVResponse {
        val response = addressService.getLocationList(request)
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }


}