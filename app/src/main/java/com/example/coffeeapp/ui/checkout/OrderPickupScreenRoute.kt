package com.example.coffeeapp.ui.checkout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.coffeeapp.navigation.Screen

@Composable
fun OrderPickupScreenRoute(
    navController: NavController,
    viewModel: OrderPickupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val cancelState by viewModel.cancelState.collectAsState()
    val expiryState by viewModel.expiryState.collectAsState()

    LaunchedEffect(uiState) {
        val state = uiState
        if (state is OrderPickupViewModel.OrderPickupUiState.Success &&
            state.order.status.trim().equals("completed", ignoreCase = true)
        ) {
            navController.navigate(Screen.OrderComplete.createRoute(state.order.orderId)) {
                popUpTo(Screen.MainScreen.route)
            }
        }
    }

    LaunchedEffect(cancelState) {
        if (cancelState is OrderPickupViewModel.CancelState.Cancelled) {
            val orderId = (uiState as? OrderPickupViewModel.OrderPickupUiState.Success)?.order?.orderId ?: ""
            navController.navigate(Screen.OrderResult.createRoute(orderId, OrderResultType.CANCELLED.name)) {
                popUpTo(Screen.MainScreen.route)
            }
        }
    }

    LaunchedEffect(expiryState) {
        if (expiryState is OrderPickupViewModel.ExpiryState.Expired) {
            val orderId = (uiState as? OrderPickupViewModel.OrderPickupUiState.Success)?.order?.orderId ?: ""
            navController.navigate(Screen.OrderResult.createRoute(orderId, OrderResultType.EXPIRED.name)) {
                popUpTo(Screen.MainScreen.route)
            }
        }
    }

    OrderPickupScreen(
        uiState = uiState,
        cancelState = cancelState,
        expiryState = expiryState,
        onToggleOtp = { viewModel.toggleOtpVisibility() },
        onNavigateBack = {
            navController.navigate(Screen.MainScreen.route) {
                popUpTo(0) { inclusive = true }
            }
        },
        onCancelTapped = { viewModel.onCancelTapped() },
        onCancelDismissed = { viewModel.onCancelDismissed() },
        onCancelConfirmed = { viewModel.onCancelConfirmed() },
        onCancelErrorDismissed = { viewModel.onCancelErrorDismissed() }
    )
}