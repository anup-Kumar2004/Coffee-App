package com.example.coffeeapp.ui.checkout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.coffeeapp.navigation.Screen

@Composable
fun OrderResultScreenRoute(
    navController: NavController,
    viewModel: OrderResultViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    OrderResultScreen(
        uiState = uiState,
        resultType = viewModel.resultType,
        onBackToHome = {
            navController.navigate(Screen.MainScreen.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    )
}