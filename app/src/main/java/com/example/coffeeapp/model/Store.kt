package com.example.coffeeapp.model

data class Store(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val city: String = "",
    val state: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0
)