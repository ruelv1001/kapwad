package dswd.ziacare.app.ui.accountcontrol.viewmodel

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import dswd.ziacare.app.data.model.ErrorModel
import dswd.ziacare.app.data.repositories.auth.AuthRepository
import dswd.ziacare.app.utils.AppConstant
import dswd.ziacare.app.utils.PopupErrorState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeoutException
import javax.inject.Inject

@HiltViewModel
class AccountControlViewModel  @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private  val _accountControlSharedFlow = MutableSharedFlow<AccountControlViewState>()
    val accountControlSharedFlow = _accountControlSharedFlow.asSharedFlow()

//    fun clearLocalData() {
//        viewModelScope.launch {
//            authRepository.clearLocalData()
//                .onStart {
//                    _accountControlSharedFlow.emit(AccountControlViewState.Loading)
//                }
//                .catch { exception ->
//                    _accountControlSharedFlow.emit(AccountControlViewState.PopupError(PopupErrorState.LocalDBError))
//                    CommonLogger.instance.sysLogE("AccountControlViewModel", exception.localizedMessage)
//                }
//                .collect {
//                    _accountControlSharedFlow.emit(
//                        AccountControlViewState.SuccessClearLocalData
//                    )
//                }
//        }
//    }

    private suspend fun onError(exception: Throwable) {
        when (exception) {
            is IOException,
            is TimeoutException,
            -> {
                _accountControlSharedFlow.emit(
                    AccountControlViewState.PopupError(
                        PopupErrorState.NetworkError
                    )
                )
            }
            is HttpException -> {
                val errorBody = exception.response()?.errorBody()
                val gson = Gson()
                val type = object : TypeToken<ErrorModel>() {}.type
                val errorResponse: ErrorModel? = gson.fromJson(errorBody?.charStream(), type)
                if (errorResponse?.has_requirements == true) {
                    _accountControlSharedFlow.emit(AccountControlViewState.InputError(errorResponse.errors))
                } else {
                    _accountControlSharedFlow.emit(
                        AccountControlViewState.PopupError(
                            if (AppConstant.isSessionStatusCode(errorResponse?.status_code.orEmpty())) {
                                PopupErrorState.SessionError
                            } else {
                                PopupErrorState.HttpError
                            }, errorResponse?.msg.orEmpty()
                        )
                    )
                }

            }
            else -> _accountControlSharedFlow.emit(
                AccountControlViewState.PopupError(
                    PopupErrorState.UnknownError
                )
            )
        }
    }
}