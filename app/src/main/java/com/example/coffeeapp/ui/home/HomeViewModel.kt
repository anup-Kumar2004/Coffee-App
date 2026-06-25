package com.example.coffeeapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeapp.data.ProductRepository
import com.example.coffeeapp.data.UserRepository
import com.example.coffeeapp.model.Category
import com.example.coffeeapp.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    sealed class HomeUiState {
        object Loading : HomeUiState()
        data class Success(
            val firstName: String,
            val products: List<Product>,
            val featuredProducts: List<Product>,
            val categories: List<Category>,
            val selectedCategory: String
        ) : HomeUiState()
        data class Error(val message: String) : HomeUiState()
    }

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading

            val userResult = userRepository.getUser()
            val productsResult = productRepository.getProducts()
            val categoriesResult = productRepository.getCategories()
            val featuredResult = productRepository.getFeaturedProducts()

            if (productsResult.isSuccess &&
                categoriesResult.isSuccess &&
                featuredResult.isSuccess
            ) {
                val products = productsResult.getOrNull() ?: emptyList()
                val categories = categoriesResult.getOrNull() ?: emptyList()
                val featured = featuredResult.getOrNull() ?: emptyList()
                val firstName = userResult.getOrNull()?.firstName ?: ""

                _uiState.value = HomeUiState.Success(
                    firstName = firstName,
                    products = products,
                    featuredProducts = featured,
                    categories = categories,
                    selectedCategory = "All"
                )
            } else {
                val error = productsResult.exceptionOrNull()
                    ?: categoriesResult.exceptionOrNull()
                    ?: featuredResult.exceptionOrNull()
                _uiState.value = HomeUiState.Error(
                    error?.message ?: "Something went wrong"
                )
            }
        }
    }

    fun onCategorySelected(category: String) {
        val currentState = _uiState.value as? HomeUiState.Success ?: return

        val filteredProducts = if (category == "All") {
            productRepository.getCachedProducts()
        } else {
            productRepository.getCachedProducts().filter { it.category == category }
        }

        _uiState.value = currentState.copy(
            selectedCategory = category,
            products = filteredProducts
        )
    }
}