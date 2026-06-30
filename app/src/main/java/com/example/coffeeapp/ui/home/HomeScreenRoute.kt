package com.example.coffeeapp.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.coffeeapp.navigation.Screen

@Composable
fun HomeScreenRoute(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val activeOrder by viewModel.activeOrder.collectAsState()
    val dismissedOrderId by viewModel.dismissedOrderId.collectAsState()

    HomeScreen(
        uiState = uiState,
        activeOrder = activeOrder,
        dismissedOrderId = dismissedOrderId,
        onProductClick = { productId ->
            navController.navigate(Screen.ProductDetail.createRoute(productId))
        },
        onCategorySelected = { category ->
            viewModel.onCategorySelected(category)
        },
        onAddToCart = { product ->
            viewModel.addToCart(product)
        },
        onTrackOrder = { orderId ->
            navController.navigate(Screen.OrderPickup.createRoute(orderId))
        },
        onDismissActiveOrder = { orderId ->
            viewModel.dismissActiveOrderBanner(orderId)
        },
        onRetry = {
            viewModel.loadData()
        }
    )
}