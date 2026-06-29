package com.example.coffeeapp.ui.checkout

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeapp.data.OrderRepository
import com.example.coffeeapp.data.ProductRepository
import com.example.coffeeapp.model.Order
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderCompleteViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository
) : ViewModel() {

    private val orderId: String = savedStateHandle.get<String>("orderId") ?: ""

    // ── UI State ──────────────────────────────────────────────────────────────

    sealed class OrderCompleteUiState {
        object Loading : OrderCompleteUiState()
        data class Success(val order: Order) : OrderCompleteUiState()
        data class Error(val message: String) : OrderCompleteUiState()
    }

    sealed class TicketState {
        object Idle : TicketState()
        object Submitting : TicketState()
        object Submitted : TicketState()
        data class Error(val message: String) : TicketState()
    }

    /**
     * Tracks the lifecycle of the product rating submission.
     * Idle       → user hasn't interacted yet
     * Submitting → waiting for all Firestore writes to complete
     * Submitted  → all writes succeeded; show "Thank you" state permanently
     * Error      → at least one write failed; user can retry
     *
     * If the order already has rated=true when loaded, we immediately
     * set this to Submitted so the rating section shows as already done.
     */
    sealed class RatingState {
        object Idle : RatingState()
        object Submitting : RatingState()
        object Submitted : RatingState()
        data class Error(val message: String) : RatingState()
    }

    private val _uiState = MutableStateFlow<OrderCompleteUiState>(OrderCompleteUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _ticketState = MutableStateFlow<TicketState>(TicketState.Idle)
    val ticketState = _ticketState.asStateFlow()

    /**
     * Map of productId → star rating (1–5) chosen by the user.
     * Starts empty — unrated products are skipped on submit.
     */
    private val _productRatings = MutableStateFlow<Map<String, Int>>(emptyMap())
    val productRatings = _productRatings.asStateFlow()

    private val _ratingState = MutableStateFlow<RatingState>(RatingState.Idle)
    val ratingState = _ratingState.asStateFlow()

    init {
        loadOrder()
    }

    private fun loadOrder() {
        viewModelScope.launch {
            _uiState.value = OrderCompleteUiState.Loading
            val result = orderRepository.getOrderById(orderId)
            if (result.isSuccess) {
                val order = result.getOrNull()!!
                _uiState.value = OrderCompleteUiState.Success(order)
                // If this order was already rated in a previous session,
                // skip straight to Submitted so the rating section is locked
                if (order.rated) {
                    _ratingState.value = RatingState.Submitted
                }
            } else {
                _uiState.value = OrderCompleteUiState.Error(
                    result.exceptionOrNull()?.message ?: "Failed to load order"
                )
            }
        }
    }

    /** Called when the user taps a star for a specific product. */
    fun setProductRating(productId: String, stars: Int) {
        _productRatings.value += (productId to stars)
    }

    /**
     * Submits ratings for all products the user has rated.
     *
     * All Firestore writes are fired in parallel using async/awaitAll —
     * no reason to wait for product A's write before starting product B's.
     *
     * After all writes succeed we mark the order as rated=true in Firestore
     * to prevent a second submission if the user returns to this screen.
     *
     * Finally we call productRepository.clearCache() so HomeScreen fetches
     * fresh ratings from Firestore instead of showing stale cached values.
     */
    fun submitProductRatings() {
        val ratings = _productRatings.value
        if (ratings.isEmpty()) return

        viewModelScope.launch {
            _ratingState.value = RatingState.Submitting

            try {
                // Fire all rating updates in parallel
                val results = ratings.map { (productId, stars) ->
                    async { productRepository.updateProductRating(productId, stars) }
                }.awaitAll()

                val failedResult = results.firstOrNull { it.isFailure }
                if (failedResult != null) {
                    _ratingState.value = RatingState.Error(
                        failedResult.exceptionOrNull()?.message
                            ?: "Some ratings could not be submitted"
                    )
                    return@launch
                }

                // Mark the order as rated to prevent duplicate submissions
                orderRepository.markOrderAsRated(orderId)

                // Invalidate cache so HomeScreen shows updated ratings
                productRepository.clearCache()

                _ratingState.value = RatingState.Submitted

            } catch (e: Exception) {
                _ratingState.value = RatingState.Error(
                    e.message ?: "Something went wrong. Please try again."
                )
            }
        }
    }

    fun resetRatingState() {
        _ratingState.value = RatingState.Idle
    }

    fun submitTicket(issueType: String, description: String) {
        viewModelScope.launch {
            _ticketState.value = TicketState.Submitting
            val result = orderRepository.submitSupportTicket(orderId, issueType, description)
            _ticketState.value = if (result.isSuccess) {
                TicketState.Submitted
            } else {
                TicketState.Error(
                    result.exceptionOrNull()?.message ?: "Failed to submit. Please try again."
                )
            }
        }
    }

    fun resetTicketState() {
        _ticketState.value = TicketState.Idle
    }
}