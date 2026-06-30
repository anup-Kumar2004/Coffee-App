package com.example.coffeeapp.ui.checkout

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeapp.data.OrderRepository
import com.example.coffeeapp.model.Order
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class OrderResultType {
    CANCELLED,
    EXPIRED
}

@HiltViewModel
class OrderResultViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val orderId: String = savedStateHandle.get<String>("orderId") ?: ""

    val resultType: OrderResultType = runCatching {
        OrderResultType.valueOf(savedStateHandle.get<String>("resultType") ?: "")
    }.getOrDefault(OrderResultType.CANCELLED)

    sealed class OrderResultUiState {
        object Loading : OrderResultUiState()
        data class Success(val order: Order) : OrderResultUiState()
        data class Error(val message: String) : OrderResultUiState()
    }

    private val _uiState = MutableStateFlow<OrderResultUiState>(OrderResultUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadOrder()
    }

    private fun loadOrder() {
        viewModelScope.launch {
            _uiState.value = OrderResultUiState.Loading
            val result = orderRepository.getOrderById(orderId)
            _uiState.value = if (result.isSuccess) {
                OrderResultUiState.Success(result.getOrNull()!!)
            } else {
                OrderResultUiState.Error(
                    result.exceptionOrNull()?.message ?: "Failed to load order"
                )
            }
        }
    }
}