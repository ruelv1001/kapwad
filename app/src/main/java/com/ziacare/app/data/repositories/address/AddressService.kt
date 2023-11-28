package com.ziacare.app.data.repositories.address

import com.ziacare.app.BuildConfig
import com.ziacare.app.data.repositories.address.request.CountryListRequest
import com.ziacare.app.data.repositories.address.request.MunicipalityListRequest
import com.ziacare.app.data.repositories.address.response.AddressResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AddressService {

    @POST("${BuildConfig.PSGC_URL}api/v1/psgc/country")
    suspend fun doGetCountryList(
        @Header("PSGC-Authorization") auth: String = BuildConfig.PSGC_AUTH,
        @Body request: CountryListRequest
    ): Response<AddressResponse>

    @POST("${BuildConfig.PSGC_URL}api/v1/psgc/province")
    suspend fun doGetProvinceList(
        @Header("PSGC-Authorization") auth: String = BuildConfig.PSGC_AUTH
    ): Response<AddressResponse>

    @POST("${BuildConfig.PSGC_URL}api/v1/psgc/citymun")
    suspend fun doGetMunicipalityList(
        @Header("PSGC-Authorization") auth: String = BuildConfig.PSGC_AUTH,
        @Body municipalityListRequest: MunicipalityListRequest
    ): Response<AddressResponse>

    @POST("${BuildConfig.PSGC_URL}api/v1/psgc/brgy")
    suspend fun doGetBarangayList(
        @Header("PSGC-Authorization") auth: String = BuildConfig.PSGC_AUTH,
        @Body municipalityListRequest: MunicipalityListRequest
    ): Response<AddressResponse>


}