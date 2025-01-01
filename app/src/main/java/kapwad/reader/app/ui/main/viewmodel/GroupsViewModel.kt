package kapwad.reader.app.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kapwad.reader.app.data.model.ErrorModel
import kapwad.reader.app.data.repositories.group.GroupRepository
import kapwad.reader.app.security.AuthEncryptedDataManager
import kapwad.reader.app.ui.group.viewmodel.GroupViewState
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
class GroupsViewModel @Inject constructor (
    private val groupRepository: GroupRepository,
    private val authEncryptedDataManager: AuthEncryptedDataManager
) : ViewModel()  {
    fun getKycStatus() :String{
        return authEncryptedDataManager.getKYCStatus()
    }

    var currentGroupCount = 0

    private val _groupSharedFlow = MutableSharedFlow<GroupViewState>()

    val groupSharedFlow: SharedFlow<GroupViewState> =
        _groupSharedFlow.asSharedFlow()


    fun doGetGroupListCount() {
        viewModelScope.launch {
            groupRepository.doGetPendingGroupListCount()
                .onStart {
                    _groupSharedFlow.emit(GroupViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _groupSharedFlow.emit(
                        GroupViewState.SuccessPendingGroupListCount(it)
                    )
                }
        }
    }

    private suspend fun onError(exception: Throwable) {
        when (exception) {
            is IOException,
            is TimeoutException,
            -> {
                _groupSharedFlow.emit(
                    GroupViewState.PopupError(
                        PopupErrorState.NetworkError
                    )
                )
            }

            is HttpException -> {
                val errorBody = exception.response()?.errorBody()
                val gson = Gson()
                val type = object : TypeToken<ErrorModel>() {}.type
                var errorResponse: ErrorModel? = gson.fromJson(errorBody?.charStream(), type)
                if (errorResponse?.has_requirements == true) {
                    _groupSharedFlow.emit(GroupViewState.InputError(errorResponse.errors))
                } else {
                    _groupSharedFlow.emit(
                        GroupViewState.PopupError(
                            if (AppConstant.isSessionStatusCode(errorResponse?.status_code.orEmpty())){
                                PopupErrorState.SessionError
                            }else{
                                PopupErrorState.HttpError
                            }
                            , errorResponse?.msg.orEmpty()
                        )
                    )
                }

            }

            else -> _groupSharedFlow.emit(
                GroupViewState.PopupError(
                    PopupErrorState.UnknownError
                )
            )
        }
    }
}