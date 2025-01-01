package kapwad.reader.app.data.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kapwad.reader.app.data.model.ErrorModel
import kapwad.reader.app.data.model.ProductOrderListModelData


import kapwad.reader.app.data.repositories.ph_market.OrderRepository
import kapwad.reader.app.security.AuthEncryptedDataManager
import kapwad.reader.app.ui.phmarket.viewmodel.OrderViewState
import kapwad.reader.app.utils.AppConstant
import kapwad.reader.app.utils.PopupErrorState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeoutException
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    authEncryptedDataManager: AuthEncryptedDataManager
) : ViewModel() {

    private val _orderStateFlow = MutableStateFlow<OrderViewState>(OrderViewState.Idle)
    val orderStateFlow: StateFlow<OrderViewState> = _orderStateFlow.asStateFlow()


    // Add or update a cart item



    fun insertOrder(order: ProductOrderListModelData) {
        viewModelScope.launch {
            orderRepository.createOrder(order)
                .onStart {
                    _orderStateFlow.emit(OrderViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _orderStateFlow.emit(OrderViewState.SuccessOfflineCreateOrder(it))
                }
        }
    }

    fun getOrder() {
        viewModelScope.launch {
            orderRepository.getOrder()
                .onStart {
                    _orderStateFlow.emit(OrderViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _orderStateFlow.emit(OrderViewState.SuccessOfflineGetOrder(it))
                }
        }
    }


    fun getTotal() {
        viewModelScope.launch {
            orderRepository.getTotals()
                .onStart {
                    _orderStateFlow.emit(OrderViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _orderStateFlow.emit(OrderViewState.SuccessTotal(it.toString()))
                }
        }
    }

    fun deleteAllOrder() {
        viewModelScope.launch {
            orderRepository.deleteAllOrder()
                .onStart {
                    _orderStateFlow.emit(OrderViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                }
                .collect {
                    _orderStateFlow.emit(OrderViewState.SuccessDelete(it.toString()))
                }
        }
    }


    private suspend fun onError(exception: Throwable) {
        when (exception) {
            is IOException,
            is TimeoutException,
            -> {
                _orderStateFlow.emit(
                    OrderViewState.PopupError(
                        PopupErrorState.NetworkError
                    )
                )
            }

            is HttpException -> {
                val errorBody = exception.response()?.errorBody()
                val gson = Gson()
                val type = object : TypeToken<ErrorModel>() {}.type
                var errorResponse: ErrorModel? = gson.fromJson(errorBody?.charStream(), type)
                _orderStateFlow.emit(
                    OrderViewState.PopupError(
                        if (AppConstant.isSessionStatusCode(errorResponse?.status_code.orEmpty())) {
                            PopupErrorState.SessionError
                        } else {
                            PopupErrorState.HttpError
                        }, errorResponse?.msg.orEmpty()
                    )
                )
            }

            else -> _orderStateFlow.emit(
                OrderViewState.PopupError(
                    PopupErrorState.UnknownError
                )
            )
        }
    }
}
