package com.example.coffeeapp.model

import com.google.firebase.Timestamp

data class Order(
    val orderId: String = "",
    val userId: String = "",
    val items: List<OrderItem> = emptyList(),
    val itemsTotal: Double = 0.0,
    val serviceFee: Double = 0.50,
    val tax: Double = 0.0,
    val grandTotal: Double = 0.0,
    val storeId: String = "",
    val storeName: String = "",
    val storeAddress: String = "",
    val otp: String = "",
    val status: String = "pending",
    val timestamp: Timestamp = Timestamp.now(),
    val rated: Boolean = false
)