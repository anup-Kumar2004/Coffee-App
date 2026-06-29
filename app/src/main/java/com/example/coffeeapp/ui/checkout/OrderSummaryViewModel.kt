package com.example.coffeeapp.ui.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeapp.data.CartRepository
import com.example.coffeeapp.model.CartItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderSummaryViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {

    companion object {
        const val SERVICE_FEE = 0.50
        const val TAX_RATE = 0.05
    }

    sealed class OrderSummaryUiState {
        object Loading : OrderSummaryUiState()
        data class Success(
            val cartItems: List<CartItem>,
            val itemsTotal: Double,
            val serviceFee: Double,
            val tax: Double,
            val grandTotal: Double
        ) : OrderSummaryUiState()
    }

    private val _uiState = MutableStateFlow<OrderSummaryUiState>(OrderSummaryUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadSummary()
    }

    private fun loadSummary() {
        viewModelScope.launch {
            cartRepository.getCartItems().collect { items ->
                val itemsTotal = items.sumOf { it.unitPrice * it.quantity }
                val tax = itemsTotal * TAX_RATE
                val grandTotal = itemsTotal + SERVICE_FEE + tax
                _uiState.value = OrderSummaryUiState.Success(
                    cartItems = items,
                    itemsTotal = itemsTotal,
                    serviceFee = SERVICE_FEE,
                    tax = tax,
                    grandTotal = grandTotal
                )
            }
        }
    }
}