package kapwad.reader.app.ui.generalSetting.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kapwad.reader.app.data.model.ErrorModel
import kapwad.reader.app.data.repositories.generalsetting.GeneralSettingRepository
import kapwad.reader.app.ui.group.viewmodel.GeneralSettingViewState
import kapwad.reader.app.utils.AppConstant
import kapwad.reader.app.utils.PopupErrorState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeoutException
import javax.inject.Inject

@HiltViewModel
class GeneralSettingViewModel @Inject constructor(
    private val generalSettingRepository: GeneralSettingRepository
) : ViewModel() {

    private val _generalSettingSharedFlow = MutableSharedFlow<GeneralSettingViewState>()

    val generalSettingSharedFlow: SharedFlow<GeneralSettingViewState> =
        _generalSettingSharedFlow.asSharedFlow()

    fun getRequestAssistanceReasons() {
        viewModelScope.launch {
            generalSettingRepository.doGetRequestAssistanceReasons()
                .onStart {
                    _generalSettingSharedFlow.emit(GeneralSettingViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _generalSettingSharedFlow.emit(
                        GeneralSettingViewState.Success(it)
                    )
                }
        }
    }

    private suspend fun onError(exception: Throwable) {
        when (exception) {
            is IOException,
            is TimeoutException,
            -> {
                _generalSettingSharedFlow.emit(
                    GeneralSettingViewState.PopupError(
                        PopupErrorState.NetworkError
                    )
                )
            }

            is HttpException -> {
                val errorBody = exception.response()?.errorBody()
                val gson = Gson()
                val type = object : TypeToken<ErrorModel>() {}.type
                var errorResponse: ErrorModel? = gson.fromJson(errorBody?.charStream(), type)
                _generalSettingSharedFlow.emit(
                    GeneralSettingViewState.PopupError(
                        if (AppConstant.isSessionStatusCode(errorResponse?.status_code.orEmpty())){
                            PopupErrorState.SessionError
                        }else{
                            PopupErrorState.HttpError
                        }
                        , errorResponse?.msg.orEmpty()
                    )
                )
            }

            else -> _generalSettingSharedFlow.emit(
                GeneralSettingViewState.PopupError(
                    PopupErrorState.UnknownError
                )
            )
        }
    }
}