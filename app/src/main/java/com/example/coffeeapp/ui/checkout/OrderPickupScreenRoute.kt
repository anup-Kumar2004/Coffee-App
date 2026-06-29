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

    // Live Firestore listener (via the ViewModel) drives this — the moment status flips
    // to "completed" in Firestore, this fires and navigates the user automatically.
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

    OrderPickupScreen(
        uiState = uiState,
        onToggleOtp = { viewModel.toggleOtpVisibility() },
        onNavigateBack = { navController.navigateUp() }
    )
}