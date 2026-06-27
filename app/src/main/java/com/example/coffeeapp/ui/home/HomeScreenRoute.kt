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
        onProductClick = { productId ->
            navController.navigate(Screen.ProductDetail.createRoute(productId))
        },
        onCategorySelected = { category ->
            viewModel.onCategorySelected(category)
        },
        onAddToCart = { product ->
            viewModel.addToCart(product)
        },
        onRetry = {
            viewModel.loadData()
        }
    )
}