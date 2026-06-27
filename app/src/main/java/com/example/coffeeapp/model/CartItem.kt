package com.example.coffeeapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val productId: String,
    val productName: String,
    val imageUrl: String,
    val cardColor: String,
    val selectedSize: String,
    val unitPrice: Double,
    val quantity: Int
)