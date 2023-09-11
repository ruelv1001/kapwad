package com.lionscare.app.data.repositories.address

import com.lionscare.app.data.repositories.address.request.LocationRequest
import com.lionscare.app.data.repositories.address.request.MunicipalityListRequest
import com.lionscare.app.data.repositories.address.request.ZoneRequest
import com.lionscare.app.data.repositories.address.response.AddressResponse
import com.lionscare.app.data.repositories.profile.response.LOVResponse
import com.lionscare.app.security.AuthEncryptedDataManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AddressRepository @Inject constructor(
    private val regRemoteDataSource: AddressRemoteDataSource,
    private val encryptedDataManager: AuthEncryptedDataManager,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {


    fun getProvinceList(): Flow<AddressResponse> {
        return flow {
            val response = regRemoteDataSource.getProvinceList()
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun getMunicipalityList(municipalityListRequest: MunicipalityListRequest): Flow<AddressResponse> {
        return flow {
            val response = regRemoteDataSource.getMunicipalityList(municipalityListRequest)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun getBarangay(municipalityListRequest: MunicipalityListRequest): Flow<AddressResponse> {
        return flow {
            val response = regRemoteDataSource.getBarangayList(municipalityListRequest)
            emit(response)
        }.flowOn(ioDispatcher)
    }


    fun getRegionList(): Flow<LOVResponse> {
        return flow {
            val response = regRemoteDataSource.getRegionList()
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun getZoneList(request: ZoneRequest): Flow<LOVResponse> {
        return flow {
            val response = regRemoteDataSource.getZoneList(request)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun getLocationList(request : LocationRequest): Flow<LOVResponse> {
        return flow {
            val response = regRemoteDataSource.getLocationList(request)
            emit(response)
        }.flowOn(ioDispatcher)
    }


}