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

    HomeScreen(
        uiState = uiState,
        onProductClick = { _ ->
            navController.navigate(Screen.ProductDetail.route)
            // Phase 6: pass productId as nav arg when ProductDetail is built
        },
        onCategorySelected = { category ->
            viewModel.onCategorySelected(category)
        },
        onRetry = {
            viewModel.loadData()
        }
    )
}