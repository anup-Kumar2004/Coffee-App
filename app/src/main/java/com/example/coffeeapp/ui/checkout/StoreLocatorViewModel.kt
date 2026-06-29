package com.example.coffeeapp.ui.checkout

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeapp.data.CartRepository
import com.example.coffeeapp.data.OrderRepository
import com.example.coffeeapp.data.StoreRepository
import com.example.coffeeapp.model.Order
import com.example.coffeeapp.model.OrderItem
import com.example.coffeeapp.model.Store
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class StoreWithDistance(
    val store: Store,
    val distanceKm: Double
)

@HiltViewModel
class StoreLocatorViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val storeRepository: StoreRepository,
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository
) : ViewModel() {

    sealed class StoreLocatorUiState {
        object Loading : StoreLocatorUiState()
        object PermissionRequired : StoreLocatorUiState()
        data class Success(val stores: List<StoreWithDistance>) : StoreLocatorUiState()
        data class Error(val message: String) : StoreLocatorUiState()
    }

    sealed class OrderPlacementState {
        object Idle : OrderPlacementState()
        object Placing : OrderPlacementState()
        data class Success(val orderId: String) : OrderPlacementState()
        data class Error(val message: String) : OrderPlacementState()
    }

    private val _uiState = MutableStateFlow<StoreLocatorUiState>(StoreLocatorUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _orderPlacementState = MutableStateFlow<OrderPlacementState>(OrderPlacementState.Idle)
    val orderPlacementState = _orderPlacementState.asStateFlow()

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    init {
        checkPermissionAndLoad()
    }

    /** Called when the screen first loads, and again after the user returns from app settings. */
    fun checkPermissionAndLoad() {
        if (hasLocationPermission()) {
            loadNearestStores()
        } else {
            _uiState.value = StoreLocatorUiState.PermissionRequired
        }
    }

    /** Called by the screen right after the system permission dialog result comes back. */
    fun onPermissionResult(granted: Boolean) {
        if (granted) {
            loadNearestStores()
        } else {
            _uiState.value = StoreLocatorUiState.PermissionRequired
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission") // permission is verified via hasLocationPermission() before this is ever called
    private fun loadNearestStores() {
        viewModelScope.launch {
            _uiState.value = StoreLocatorUiState.Loading
            try {
                val location = fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    CancellationTokenSource().token
                ).await()

                if (location == null) {
                    _uiState.value = StoreLocatorUiState.Error(
                        "Unable to get your current location. Please make sure location is turned on and try again."
                    )
                    return@launch
                }

                val storesResult = storeRepository.getStores()
                if (storesResult.isFailure) {
                    _uiState.value = StoreLocatorUiState.Error(
                        storesResult.exceptionOrNull()?.message ?: "Failed to load stores"
                    )
                    return@launch
                }

                val stores = storesResult.getOrNull() ?: emptyList()
                val nearestStores = stores
                    .map { store ->
                        StoreWithDistance(
                            store = store,
                            distanceKm = haversineDistance(
                                location.latitude, location.longitude,
                                store.lat, store.lng
                            )
                        )
                    }
                    .sortedBy { it.distanceKm }
                    .take(10)

                _uiState.value = StoreLocatorUiState.Success(nearestStores)
            } catch (e: Exception) {
                _uiState.value = StoreLocatorUiState.Error(e.message ?: "Something went wrong")
            }
        }
    }

    private fun haversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadiusKm = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadiusKm * c
    }

    /**
     * Called when the user taps "Select this Store". Builds the order from the current
     * Room cart, writes it to Firestore, clears the cart, and exposes the new orderId
     * via [orderPlacementState] for the screen to navigate on.
     */
    fun placeOrder(store: Store) {
        viewModelScope.launch {
            _orderPlacementState.value = OrderPlacementState.Placing
            try {
                val cartItems = cartRepository.getCartItems().first()
                if (cartItems.isEmpty()) {
                    _orderPlacementState.value = OrderPlacementState.Error("Your cart is empty.")
                    return@launch
                }

                val orderItems = cartItems.map { item ->
                    OrderItem(
                        productId = item.productId,
                        productName = item.productName,
                        selectedSize = item.selectedSize,
                        quantity = item.quantity,
                        unitPrice = item.unitPrice,
                        lineTotal = item.unitPrice * item.quantity
                    )
                }

                val itemsTotal = orderItems.sumOf { it.lineTotal }
                val serviceFee = OrderSummaryViewModel.SERVICE_FEE
                val tax = itemsTotal * OrderSummaryViewModel.TAX_RATE
                val grandTotal = itemsTotal + serviceFee + tax
                val otp = (100000..999999).random().toString()

                val order = Order(
                    userId = orderRepository.getCurrentUserId(),
                    items = orderItems,
                    itemsTotal = itemsTotal,
                    serviceFee = serviceFee,
                    tax = tax,
                    grandTotal = grandTotal,
                    storeId = store.id,
                    storeName = store.name,
                    storeAddress = store.address,
                    otp = otp,
                    status = "pending"
                )

                val result = orderRepository.placeOrder(order)
                if (result.isSuccess) {
                    cartRepository.clearCart()
                    _orderPlacementState.value = OrderPlacementState.Success(result.getOrNull() ?: "")
                } else {
                    _orderPlacementState.value = OrderPlacementState.Error(
                        result.exceptionOrNull()?.message ?: "Failed to place order. Please try again."
                    )
                }
            } catch (e: Exception) {
                _orderPlacementState.value = OrderPlacementState.Error(e.message ?: "Something went wrong")
            }
        }
    }

    /** Resets order placement state back to Idle, e.g. after showing/dismissing an error. */
    fun resetOrderPlacementState() {
        _orderPlacementState.value = OrderPlacementState.Idle
    }
}