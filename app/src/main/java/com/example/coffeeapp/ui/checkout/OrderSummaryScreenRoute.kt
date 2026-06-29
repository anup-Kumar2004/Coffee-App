package com.example.coffeeapp.ui.checkout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.coffeeapp.navigation.Screen

@Composable
fun OrderSummaryScreenRoute(
    navController: NavController,
    viewModel: OrderSummaryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    OrderSummaryScreen(
        uiState = uiState,
        onNavigateBack = { navController.navigateUp() },
        onProceedToStoreLocator = {
            navController.navigate(Screen.StoreLocator.route)
        }
    )
}