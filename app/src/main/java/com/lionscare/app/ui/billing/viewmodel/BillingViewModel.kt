package com.lionscare.app.ui.billing.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lionscare.app.data.model.ErrorModel
import com.lionscare.app.data.repositories.group.GroupRepository
import com.lionscare.app.security.AuthEncryptedDataManager
import com.lionscare.app.ui.billing.viewstate.BillingViewState
import com.lionscare.app.ui.main.viewmodel.GroupListViewState
import com.lionscare.app.ui.main.viewmodel.ImmediateFamilyViewState
import com.lionscare.app.utils.AppConstant
import com.lionscare.app.utils.CommonLogger
import com.lionscare.app.utils.PopupErrorState
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
class BillingViewModel @Inject constructor(
    private val encryptedDataManager: AuthEncryptedDataManager,
    private val groupRepository: GroupRepository
) : ViewModel() {
    // Shared Flows
    private val _billingSharedFlow = MutableSharedFlow<BillingViewState>()
    val billingSharedFlow: SharedFlow<BillingViewState> =
        _billingSharedFlow.asSharedFlow()

    private val _getFamilySharedFlow = MutableSharedFlow<ImmediateFamilyViewState>()
    val immediateFamilySharedFlow: SharedFlow<ImmediateFamilyViewState> =
        _getFamilySharedFlow.asSharedFlow()

    private val _getGroupSharedFlow = MutableSharedFlow<GroupListViewState>()
    val getGroupSharedFlow: SharedFlow<GroupListViewState> =
        _getGroupSharedFlow.asSharedFlow()

    //=================== For ask for donations group request and custom request
    var immediateFamilyId = ""
    var billingStatementNumber = "Billing Statement B-0000004"

    fun loadGroups() {
        viewModelScope.launch {
            groupRepository.doGetGroupList()
                .cachedIn(viewModelScope)
                .onStart {
                    _getGroupSharedFlow.emit(GroupListViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                    CommonLogger.devLog("error", exception)
                }
                .collect { pagingData ->
                    _getGroupSharedFlow.emit(
                        GroupListViewState.Success(pagingData)
                    )
                }
        }
    }

    fun getImmediateFamily() {
        viewModelScope.launch {
            groupRepository.doGetImmediateFamily()
                .onStart {
                    _getFamilySharedFlow.emit(ImmediateFamilyViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                    CommonLogger.sysLogE("ERROR", exception)
                }
                .collect {
                    _getFamilySharedFlow.emit(
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
                _billingSharedFlow.emit(
                    BillingViewState.PopupError(
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
                    _billingSharedFlow.emit(BillingViewState.InputError(errorResponse.errors))
                } else {
                    if (AppConstant.isSessionStatusCode(errorResponse?.status_code.orEmpty())) {
                        _billingSharedFlow.emit(
                            BillingViewState.PopupError(
                                PopupErrorState.SessionError,
                                errorResponse?.msg.orEmpty()
                            )
                        )
                    } else if (errorResponse?.status_code.orEmpty() != AppConstant.NOT_FOUND) {
                        _billingSharedFlow.emit(
                            BillingViewState.PopupError(
                                PopupErrorState.HttpError,
                                errorResponse?.msg.orEmpty()
                            )
                        )
                    }
                }
            }

            else -> _billingSharedFlow.emit(
                BillingViewState.PopupError(
                    PopupErrorState.UnknownError
                )
            )
        }
    }
}