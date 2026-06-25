package com.example.coffeeapp.navigation

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object MainScreen : Screen("main")
    object Home : Screen("home")
    object Cart : Screen("cart")
    object OrderHistory : Screen("order_history")
    object Profile : Screen("profile")
    object ProductDetail : Screen("product_detail")
    object StorePanel : Screen("dev_store_panel")
}