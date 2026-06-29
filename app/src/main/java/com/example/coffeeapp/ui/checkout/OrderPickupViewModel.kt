package com.example.coffeeapp.ui.checkout

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeapp.data.OrderRepository
import com.example.coffeeapp.model.Order
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

private const val ORDER_VALID_MINUTES = 30L

@HiltViewModel
class OrderPickupViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val orderId: String = savedStateHandle.get<String>("orderId") ?: ""

    sealed class OrderPickupUiState {
        object Loading : OrderPickupUiState()
        data class Success(
            val order: Order,
            val isOtpRevealed: Boolean,
            val remainingSeconds: Long
        ) : OrderPickupUiState()
        data class Error(val message: String) : OrderPickupUiState()
    }

    private val _uiState = MutableStateFlow<OrderPickupUiState>(OrderPickupUiState.Loading)
    val uiState = _uiState.asStateFlow()

    // Survives across Firestore re-emissions so the OTP doesn't re-hide on every status update.
    private var isOtpRevealed = false
    private var countdownJob: Job? = null

    init {
        observeOrder()
    }

    private fun observeOrder() {
        viewModelScope.launch {
            orderRepository.listenToOrder(orderId).collect { result ->
                if (result.isSuccess) {
                    val order = result.getOrNull()!!
                    _uiState.value = OrderPickupUiState.Success(
                        order = order,
                        isOtpRevealed = isOtpRevealed,
                        remainingSeconds = computeRemainingSeconds(order)
                    )
                    startCountdownIfNeeded()
                } else {
                    _uiState.value = OrderPickupUiState.Error(
                        result.exceptionOrNull()?.message ?: "Failed to load order"
                    )
                }
            }
        }
    }

    private fun computeRemainingSeconds(order: Order): Long {
        val placedAtMillis = order.timestamp.toDate().time
        val expiryMillis = placedAtMillis + ORDER_VALID_MINUTES * 60 * 1000
        val remainingMillis = expiryMillis - System.currentTimeMillis()
        return (remainingMillis / 1000).coerceAtLeast(0)
    }

    private fun startCountdownIfNeeded() {
        if (countdownJob?.isActive == true) return
        countdownJob = viewModelScope.launch {
            while (true) {
                delay(1000.milliseconds)
                val current = _uiState.value as? OrderPickupUiState.Success ?: break
                val remaining = computeRemainingSeconds(current.order)
                _uiState.value = current.copy(remainingSeconds = remaining)
                if (remaining <= 0) break
            }
        }
    }

    /** Called when the user taps the reveal/hide OTP button. */
    fun toggleOtpVisibility() {
        isOtpRevealed = !isOtpRevealed
        val current = _uiState.value as? OrderPickupUiState.Success ?: return
        _uiState.value = current.copy(isOtpRevealed = isOtpRevealed)
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }
}