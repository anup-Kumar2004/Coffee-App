package com.example.coffeeapp.model

data class OrderItem(
    val productId: String = "",
    val productName: String = "",
    val selectedSize: String = "",
    val quantity: Int = 0,
    val unitPrice: Double = 0.0,
    val lineTotal: Double = 0.0
)