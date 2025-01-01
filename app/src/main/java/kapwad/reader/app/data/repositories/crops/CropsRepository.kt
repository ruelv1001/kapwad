package kapwad.reader.app.data.repositories.crops

import androidx.paging.PagingConfig
import kapwad.reader.app.data.repositories.baseresponse.GeneralResponse
import kapwad.reader.app.data.repositories.crops.request.CropDetailsRequest
import kapwad.reader.app.data.repositories.crops.request.UploadImageRequest
import kapwad.reader.app.data.repositories.crops.request.UploadVideRequest
import kapwad.reader.app.data.repositories.crops.response.CropDetailsResponse
import kapwad.reader.app.data.repositories.crops.response.CropItemListResponse
import kapwad.reader.app.data.repositories.crops.response.CropsResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class CropsRepository @Inject constructor(
    private val cropsRemoteDataSource: CropsRemoteDataSource,
    private val cropsPagingSource: CropsPagingSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {




//
//    fun getAllCrops(pagingConfig: PagingConfig = getDefaultPageConfig()): Flow<PagingData<CropsData>> {
//        return Pager(
//            config = pagingConfig,
//            pagingSourceFactory = { cropsPagingSource }
//        ).flow
//            .flowOn(ioDispatcher)
//    }

    fun getAllCrops(): Flow<CropsResponse> {
        return flow {
            val response = cropsRemoteDataSource.doGetAllCropsList()
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun getAllCropsItem(cropId:String): Flow<CropItemListResponse> {
        return flow {
            val response = cropsRemoteDataSource.getCropItemList(cropId)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun getStartCrop(): Flow<GeneralResponse> {
        return flow {
            val response = cropsRemoteDataSource.getStartCrop()
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun doUploadVideo(request: UploadVideRequest): Flow<GeneralResponse> {
        return flow {
            val response = cropsRemoteDataSource.doUploadVideo(request)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun doUploadImage(request: UploadImageRequest): Flow<GeneralResponse> {
        return flow {
            val response = cropsRemoteDataSource.doUploadImage(request)
            emit(response)
        }.flowOn(ioDispatcher)
    }





    fun dogetCropDetails(cropDetailsRequest: CropDetailsRequest): Flow<CropDetailsResponse> {
        return cropsRemoteDataSource.getCropDetails(cropDetailsRequest)
            .flowOn(ioDispatcher)
    }

    private fun getDefaultPageConfig(): PagingConfig {
        return PagingConfig(pageSize = 1,enablePlaceholders = false)
    }

}