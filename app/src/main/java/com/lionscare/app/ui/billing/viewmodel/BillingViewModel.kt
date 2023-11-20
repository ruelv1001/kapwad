package com.lionscare.app.ui.billing.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lionscare.app.data.model.ErrorModel
import com.lionscare.app.data.repositories.billing.BillRepository
import com.lionscare.app.data.repositories.group.GroupRepository
import com.lionscare.app.data.repositories.group.response.GroupListData
import com.lionscare.app.security.AuthEncryptedDataManager
import com.lionscare.app.ui.billing.viewstate.BillingViewState
import com.lionscare.app.ui.billing.viewstate.CustomGroupListDataModel
import com.lionscare.app.ui.billing.viewstate.CustomMemberListDataModel
import com.lionscare.app.ui.bulletin.viewmodel.BillViewState
import com.lionscare.app.ui.group.viewmodel.MemberViewState
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
    private val groupRepository: GroupRepository,
    private val billRepository: BillRepository
) : ViewModel() {
    // Shared Flows
    private val _billingSharedFlow = MutableSharedFlow<BillingViewState>()
    val billingSharedFlow: SharedFlow<BillingViewState> =
        _billingSharedFlow.asSharedFlow()

    //=================== For ask for donations group request and custom request

    var shouldShowDonationRequestsViews = true // for donations request list

    var isRequestFromGroups = false //if it came from groups or personal request screen
    var immediateFamilyData: GroupListData? = null

    //================= For holding data, to cache users selected groups and custom people request in donations
    var groupsRequestsData: MutableList<CustomGroupListDataModel>? = null
    var customRequestsData: MutableList<CustomMemberListDataModel>? = null

    var currentFragmentRoute: String =
        "" // determines if it should show list from API or cached list above

    var billingStatementNumber = "B-0000004"


    //Ask for donations
    fun loadRequests(code: String) {
        viewModelScope.launch {
            loadGroupsRequestedForDonations(code)
        }
    }

    private suspend fun loadGroupsRequestedForDonations(code: String) {
        billRepository.doGetAllListOfGroupRequestedForDonations(code = code)
            .cachedIn(viewModelScope)
            .onStart {
                _billingSharedFlow.emit(BillingViewState.LoadingMembers)
            }
            .catch { exception ->
                onError(exception)
            }
            .collect { pagingData ->
                _billingSharedFlow.emit(
                    BillingViewState.SuccessGroupsRequestedForDonations(pagingData)
                )
            }
    }


    fun loadGroups() {
        viewModelScope.launch {
            doGetGroupList()
        }
    }

    private suspend fun doGetGroupList() {
        groupRepository.doGetGroupList()
            .cachedIn(viewModelScope)
            .onStart {
                _billingSharedFlow.emit(BillingViewState.LoadingGroups)
            }
            .catch { exception ->
                onError(exception)
                CommonLogger.devLog("error", exception)
            }
            .collect { pagingData ->
                _billingSharedFlow.emit(
                    BillingViewState.SuccessLoadGroup(pagingData)
                )
            }
    }

    fun getImmediateFamily() {
        viewModelScope.launch {
            groupRepository.doGetImmediateFamily()
                .onStart {
                    _billingSharedFlow.emit(BillingViewState.LoadingFamily)
                }
                .catch { exception ->
                    onError(exception, endpoint = "family")
                    CommonLogger.sysLogE("ERROR", exception)
                }
                .collect {
                    _billingSharedFlow.emit(
                        BillingViewState.SuccessLoadFamily(it)
                    )
                }
        }
    }

    private suspend fun loadMyBills(status: String) {
        billRepository.doGetAllMyBillList(status = status)
            .cachedIn(viewModelScope)
            .onStart {
                _billingSharedFlow.emit(BillingViewState.LoadingMyBills)
            }
            .catch { exception ->
                onError(exception)
                CommonLogger.devLog("error",exception)
            }
            .collect { pagingData ->
                _billingSharedFlow.emit(
                    BillingViewState.SuccessMyListOfBills(pagingData)
                )
            }
    }

    fun refreshMyOngoingBills() {
        viewModelScope.launch {
            loadMyBills("ongoing")
        }
    }

    fun refreshMyCompletedBills() {
        viewModelScope.launch {
            loadMyBills("completed")
        }
    }


    private suspend fun onError(exception: Throwable, endpoint: String = "") {
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
                                errorResponse?.msg.orEmpty(),
                            )
                        )
                    } else {
                        _billingSharedFlow.emit(
                            BillingViewState.PopupError(
                                PopupErrorState.HttpError,
                                errorResponse?.msg.orEmpty(),
                                endpoint = endpoint
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