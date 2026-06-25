package com.example.coffeeapp.model

data class Product(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val category: String = "",
    val imageUrl: String = "",
    val cardColor: String = "#966E58",
    @get:JvmName("getIsAvailable")
    val isAvailable: Boolean = true,
    @get:JvmName("getIsFeatured")
    val isFeatured: Boolean = false,
    val rating: Double = 0.0,
    val totalRatings: Int = 0,
    val sizes: Map<String, Double> = emptyMap()
)