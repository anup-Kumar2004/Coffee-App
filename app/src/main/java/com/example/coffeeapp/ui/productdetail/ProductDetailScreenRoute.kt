package com.example.coffeeapp.ui.productdetail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun ProductDetailScreenRoute(
    navController: NavController,
    viewModel: ProductDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    ProductDetailScreen(
        uiState = uiState,
        onSizeSelected = viewModel::onSizeSelected,
        onIncrementQuantity = viewModel::incrementQuantity,
        onDecrementQuantity = viewModel::decrementQuantity,
        onToggleFavorite = viewModel::toggleFavorite,
        onAddToCart = viewModel::addToCart,
        onNavigateBack = { navController.navigateUp() }
    )
}