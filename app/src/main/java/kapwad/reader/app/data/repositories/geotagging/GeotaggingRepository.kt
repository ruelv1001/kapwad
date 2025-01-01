package kapwad.reader.app.data.repositories.geotagging

import kapwad.reader.app.data.repositories.baseresponse.GeneralResponse
import kapwad.reader.app.data.repositories.geotagging.request.GeotaggingUploadRequest
import kapwad.reader.app.data.repositories.geotagging.response.GeoTaggingResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GeotaggingRepository @Inject constructor(
    private val geoTaggingRemoteDataSource: GeoTaggingRemoteDataSource,

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {











    fun doUploadImage(request: GeotaggingUploadRequest): Flow<GeneralResponse> {
        return flow {
            val response = geoTaggingRemoteDataSource.doUploadImage(request)
            emit(response)
        }.flowOn(ioDispatcher)
    }





    fun doGetStatus(): Flow<GeoTaggingResponse> {
        return geoTaggingRemoteDataSource.getGetaggingStatus()
            .flowOn(ioDispatcher)
    }



}