package dswd.ziacare.app.data.repositories.generalsetting

import dswd.ziacare.app.data.repositories.generalsetting.response.RequestAssistanceLOVResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GeneralSettingRepository @Inject constructor(
    private val generalSettingRemoteDataSource: GeneralSettingRemoteDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    fun doGetRequestAssistanceReasons(): Flow<RequestAssistanceLOVResponse> {
        return flow {
            val response = generalSettingRemoteDataSource.doGetRequestAssistanceReasons()
            emit(response)
        }.flowOn(ioDispatcher)
    }

}