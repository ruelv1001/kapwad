package dswd.ziacare.app.data.repositories.address

import dswd.ziacare.app.data.repositories.address.request.MunicipalityListRequest
import dswd.ziacare.app.data.repositories.address.response.AddressResponse
import dswd.ziacare.app.security.AuthEncryptedDataManager
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

    fun getCountryList(displayAllowedCountries: Boolean): Flow<AddressResponse> {
        return flow {
            val response = regRemoteDataSource.getCountryList(displayAllowedCountries)
            emit(response)
        }.flowOn(ioDispatcher)
    }
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
}