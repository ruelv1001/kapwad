package da.farmer.app.ui.accountcontrol.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import da.farmer.app.data.model.ErrorModel
import da.farmer.app.data.repositories.auth.AuthRepository
import da.farmer.app.utils.AppConstant
import da.farmer.app.utils.PopupErrorState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
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


    fun deleteOrDeactivateAccount(reasonId: String, other: String? =null, type: String) {
        viewModelScope.launch {
            authRepository.doDeleteOrDeactivateAccount(reasonId = reasonId, other = other, type = type)
                .onStart {
                    _accountControlSharedFlow.emit(AccountControlViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _accountControlSharedFlow.emit(
                        AccountControlViewState.SuccessDeleteOrDeactivateAccount(it)
                    )
                }
        }
    }

    fun deleteOrDeactivateAccountOTP(reasonId: String, other: String? =null, type: String, otp: String) {
        viewModelScope.launch {
            authRepository.doDeleteOrDeactivateAccountOTP(reasonId = reasonId, other = other, type = type, otp = otp )
                .onStart {
                    _accountControlSharedFlow.emit(AccountControlViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _accountControlSharedFlow.emit(
                        AccountControlViewState.SuccessDeleteOrDeactivateAccountOTP(it)
                    )
                }
        }
    }

    fun getReasonsList() {
        viewModelScope.launch {
            authRepository.getReasonsList()
                .onStart {
                    _accountControlSharedFlow.emit(AccountControlViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _accountControlSharedFlow.emit(
                        AccountControlViewState.SuccessGetReasons(it)
                    )
                }
        }
    }

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