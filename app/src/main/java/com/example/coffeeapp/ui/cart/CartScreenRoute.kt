package com.example.coffeeapp.ui.cart

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.coffeeapp.navigation.Screen

@Composable
fun CartScreenRoute(
    navController: NavController,
    viewModel: CartViewModel = hiltViewModel()
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val totalPrice by viewModel.totalPrice.collectAsState()

    CartScreen(
        cartItems = cartItems,
        totalPrice = totalPrice,
        onIncrement = viewModel::incrementQuantity,
        onDecrement = viewModel::decrementQuantity,
        onRemove = viewModel::removeItem,
        onClearCart = viewModel::clearCart,
        onProceedToCheckout = { navController.navigate(Screen.OrderSummary.route) }
    )
}