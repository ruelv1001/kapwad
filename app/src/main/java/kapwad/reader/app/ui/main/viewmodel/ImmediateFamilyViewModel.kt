package kapwad.reader.app.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kapwad.reader.app.data.model.ErrorModel
import kapwad.reader.app.data.repositories.group.GroupRepository
import kapwad.reader.app.utils.AppConstant
import kapwad.reader.app.utils.CommonLogger
import kapwad.reader.app.utils.PopupErrorState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.concurrent.TimeoutException
import javax.inject.Inject
import retrofit2.HttpException

@HiltViewModel
class ImmediateFamilyViewModel  @Inject constructor(private val groupRepository: GroupRepository) :
    ViewModel() {

    private val _getGroupSharedFlow = MutableSharedFlow<ImmediateFamilyViewState>()
    val immediateFamilySharedFlow: SharedFlow<ImmediateFamilyViewState> =
        _getGroupSharedFlow.asSharedFlow()

    fun getImmediateFamily() {
        viewModelScope.launch {
            groupRepository.doGetImmediateFamily()
                .onStart {
                    _getGroupSharedFlow.emit(ImmediateFamilyViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                    CommonLogger.sysLogE("ERROR", exception)
                }
                .collect {
                    _getGroupSharedFlow.emit(
                        ImmediateFamilyViewState.Success(it)
                    )
                }
        }
    }

    private suspend fun onError(exception: Throwable) {
        when (exception) {
            is IOException,
            is TimeoutException,
            -> {
                _getGroupSharedFlow.emit(
                    ImmediateFamilyViewState.PopupError(
                        PopupErrorState.NetworkError
                    )
                )
            }
            is HttpException -> {
                val errorBody = exception.response()?.errorBody()
                val gson = Gson()
                val type = object : TypeToken<ErrorModel>() {}.type
                var errorResponse: ErrorModel? = gson.fromJson(errorBody?.charStream(), type)

                if(AppConstant.isSessionStatusCode(errorResponse?.status_code.orEmpty())){
                    _getGroupSharedFlow.emit(
                        ImmediateFamilyViewState.PopupError(
                            PopupErrorState.SessionError,
                            errorResponse?.msg.orEmpty()
                        )
                    )
                }else{
                    _getGroupSharedFlow.emit(
                        ImmediateFamilyViewState.PopupError(
                            PopupErrorState.HttpError,
                            errorResponse?.msg.orEmpty()
                        )
                    )
                }
            }
            else -> _getGroupSharedFlow.emit(
                ImmediateFamilyViewState.PopupError(
                    PopupErrorState.UnknownError
                )
            )
        }
    }
}