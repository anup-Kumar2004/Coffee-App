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
import kotlin.time.Duration.Companion.seconds

private const val ORDER_VALID_MINUTES = 30L
private const val CANCEL_WINDOW_SECONDS = 90L
private const val RESULT_OVERLAY_DISPLAY_MILLIS = 2500L

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
            val remainingSeconds: Long,
            val cancelButtonVisible: Boolean,
            val cancelButtonEnabled: Boolean
        ) : OrderPickupUiState()
        data class Error(val message: String) : OrderPickupUiState()
    }

    sealed class CancelState {
        object Idle : CancelState()
        object Confirming : CancelState()
        object Cancelling : CancelState()
        object Cancelled : CancelState()
        data class Error(val message: String) : CancelState()
    }

    sealed class ExpiryState {
        object None : ExpiryState()
        object Expiring : ExpiryState()
        object Expired : ExpiryState()
    }

    private val _uiState = MutableStateFlow<OrderPickupUiState>(OrderPickupUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _cancelState = MutableStateFlow<CancelState>(CancelState.Idle)
    val cancelState = _cancelState.asStateFlow()

    private val _expiryState = MutableStateFlow<ExpiryState>(ExpiryState.None)
    val expiryState = _expiryState.asStateFlow()

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
                    val status = order.status.trim().lowercase()

                    when (status) {
                        "cancelled" -> {
                            if (_cancelState.value !is CancelState.Cancelling &&
                                _cancelState.value !is CancelState.Cancelled
                            ) {
                                handleExternalCancellation()
                            }
                            return@collect
                        }
                        "expired" -> {
                            if (_expiryState.value !is ExpiryState.Expiring &&
                                _expiryState.value !is ExpiryState.Expired
                            ) {
                                handleExternalExpiry()
                            }
                            return@collect
                        }
                        "completed" -> {
                            _uiState.value = OrderPickupUiState.Success(
                                order = order,
                                isOtpRevealed = isOtpRevealed,
                                remainingSeconds = 0,
                                cancelButtonVisible = false,
                                cancelButtonEnabled = false
                            )
                            return@collect
                        }
                    }

                    val remaining = computeRemainingSeconds(order)
                    val secondsSincePlacement = computeSecondsSincePlacement(order)
                    val cancelStillAllowed = secondsSincePlacement < CANCEL_WINDOW_SECONDS

                    _uiState.value = OrderPickupUiState.Success(
                        order = order,
                        isOtpRevealed = isOtpRevealed,
                        remainingSeconds = remaining,
                        cancelButtonVisible = cancelStillAllowed,
                        cancelButtonEnabled = cancelStillAllowed
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

    private fun computeSecondsSincePlacement(order: Order): Long {
        val placedAtMillis = order.timestamp.toDate().time
        return ((System.currentTimeMillis() - placedAtMillis) / 1000).coerceAtLeast(0)
    }

    private fun startCountdownIfNeeded() {
        if (countdownJob?.isActive == true) return
        countdownJob = viewModelScope.launch {
            while (true) {
                delay(1.seconds)
                val current = _uiState.value as? OrderPickupUiState.Success ?: break
                val remaining = computeRemainingSeconds(current.order)
                val secondsSincePlacement = computeSecondsSincePlacement(current.order)

                if (remaining <= 0) {
                    triggerExpiry()
                    break
                }

                val cancelStillAllowed = secondsSincePlacement < CANCEL_WINDOW_SECONDS

                _uiState.value = current.copy(
                    remainingSeconds = remaining,
                    cancelButtonVisible = cancelStillAllowed,
                    cancelButtonEnabled = cancelStillAllowed
                )
            }
        }
    }

    private fun triggerExpiry() {
        if (_expiryState.value != ExpiryState.None) return
        viewModelScope.launch {
            _expiryState.value = ExpiryState.Expiring
            orderRepository.updateOrderStatus(orderId, "expired")
            delay(RESULT_OVERLAY_DISPLAY_MILLIS.milliseconds)
            _expiryState.value = ExpiryState.Expired
        }
    }

    private fun handleExternalCancellation() {
        viewModelScope.launch {
            _cancelState.value = CancelState.Cancelling
            delay(RESULT_OVERLAY_DISPLAY_MILLIS.milliseconds)
            _cancelState.value = CancelState.Cancelled
        }
    }

    private fun handleExternalExpiry() {
        viewModelScope.launch {
            _expiryState.value = ExpiryState.Expiring
            delay(RESULT_OVERLAY_DISPLAY_MILLIS.milliseconds)
            _expiryState.value = ExpiryState.Expired
        }
    }

    fun toggleOtpVisibility() {
        isOtpRevealed = !isOtpRevealed
        val current = _uiState.value as? OrderPickupUiState.Success ?: return
        _uiState.value = current.copy(isOtpRevealed = isOtpRevealed)
    }

    fun onCancelTapped() {
        _cancelState.value = CancelState.Confirming
    }

    fun onCancelDismissed() {
        _cancelState.value = CancelState.Idle
    }

    fun onCancelConfirmed() {
        if (_cancelState.value is CancelState.Cancelling) return
        viewModelScope.launch {
            _cancelState.value = CancelState.Cancelling
            val result = orderRepository.updateOrderStatus(orderId, "cancelled")
            if (result.isSuccess) {
                delay(RESULT_OVERLAY_DISPLAY_MILLIS.milliseconds)
                _cancelState.value = CancelState.Cancelled
            } else {
                _cancelState.value = CancelState.Error(
                    result.exceptionOrNull()?.message ?: "Failed to cancel order. Please try again."
                )
            }
        }
    }

    fun onCancelErrorDismissed() {
        _cancelState.value = CancelState.Idle
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }
}