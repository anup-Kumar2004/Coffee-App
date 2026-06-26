package com.example.coffeeapp.ui.productdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.coffeeapp.data.ProductRepository
import com.example.coffeeapp.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

private val SIZE_ORDER = listOf("small", "medium", "large", "regular")

fun sortedSizeKeys(sizes: Map<String, Double>): List<String> {
    return sizes.keys.sortedWith(
        compareBy(
            { SIZE_ORDER.indexOf(it.lowercase()).let { idx -> if (idx == -1) Int.MAX_VALUE else idx } },
            { it }
        )
    )
}

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val productRepository: ProductRepository
) : ViewModel() {

    sealed class ProductDetailUiState {
        object Loading : ProductDetailUiState()
        data class Success(
            val product: Product,
            val selectedSize: String,
            val quantity: Int,
            val isFavorited: Boolean
        ) : ProductDetailUiState()
        data class Error(val message: String) : ProductDetailUiState()
    }

    private val productId: String = savedStateHandle.get<String>("productId") ?: ""

    private val _uiState = MutableStateFlow<ProductDetailUiState>(ProductDetailUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadProduct()
    }

    private fun loadProduct() {
        val product = productRepository.getProductById(productId)
        _uiState.value = if (product != null) {
            ProductDetailUiState.Success(
                product = product,
                selectedSize = sortedSizeKeys(product.sizes).firstOrNull() ?: "",
                quantity = 1,
                isFavorited = false
            )
        } else {
            ProductDetailUiState.Error("Product not found")
        }
    }

    fun onSizeSelected(size: String) {
        val current = _uiState.value as? ProductDetailUiState.Success ?: return
        _uiState.value = current.copy(selectedSize = size)
    }

    fun incrementQuantity() {
        val current = _uiState.value as? ProductDetailUiState.Success ?: return
        if (current.quantity < 10) {
            _uiState.value = current.copy(quantity = current.quantity + 1)
        }
    }

    fun decrementQuantity() {
        val current = _uiState.value as? ProductDetailUiState.Success ?: return
        if (current.quantity > 1) {
            _uiState.value = current.copy(quantity = current.quantity - 1)
        }
    }

    fun toggleFavorite() {
        val current = _uiState.value as? ProductDetailUiState.Success ?: return
        _uiState.value = current.copy(isFavorited = !current.isFavorited)
    }
}