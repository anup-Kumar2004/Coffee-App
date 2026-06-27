package com.example.coffeeapp.data

import com.example.coffeeapp.model.CartItem
import com.example.coffeeapp.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepository @Inject constructor(
    private val cartDao: CartDao
) {
    fun getCartItems(): Flow<List<CartItem>> {
        return cartDao.getAllCartItems()
    }

    fun getCartItemCount(): Flow<Int> {
        return cartDao.getAllCartItems().map { items -> items.sumOf { it.quantity } }
    }

    suspend fun addToCart(product: Product, selectedSize: String, quantity: Int) {
        val unitPrice = product.sizes[selectedSize] ?: 0.0
        val existing = cartDao.findCartItem(product.id, selectedSize)

        if (existing != null) {
            cartDao.updateCartItem(existing.copy(quantity = existing.quantity + quantity))
        } else {
            cartDao.insertCartItem(
                CartItem(
                    productId = product.id,
                    productName = product.name,
                    imageUrl = product.imageUrl,
                    cardColor = product.cardColor,
                    selectedSize = selectedSize,
                    unitPrice = unitPrice,
                    quantity = quantity
                )
            )
        }
    }

    suspend fun updateQuantity(cartItem: CartItem, newQuantity: Int) {
        if (newQuantity <= 0) {
            cartDao.deleteCartItem(cartItem)
        } else {
            cartDao.updateCartItem(cartItem.copy(quantity = newQuantity))
        }
    }

    suspend fun removeItem(cartItem: CartItem) {
        cartDao.deleteCartItem(cartItem)
    }

    suspend fun clearCart() {
        cartDao.clearCart()
    }
}