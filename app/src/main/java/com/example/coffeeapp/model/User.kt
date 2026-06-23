package com.example.coffeeapp.model

data class User(
    val uid: String,
    val email: String,
    val firstName: String = "",
    val lastName: String = ""
)