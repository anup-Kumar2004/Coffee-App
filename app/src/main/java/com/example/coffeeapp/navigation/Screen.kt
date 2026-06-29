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
    object ProductDetail : Screen("product_detail/{productId}") {
        fun createRoute(productId: String) = "product_detail/$productId"
    }
    object StorePanel : Screen("dev_store_panel")
    object OrderSummary : Screen("order_summary")
    object StoreLocator : Screen("store_locator")
    object OrderPickup : Screen("order_pickup/{orderId}") {
        fun createRoute(orderId: String) = "order_pickup/$orderId"
    }
    object OrderComplete : Screen("order_complete/{orderId}") {
        fun createRoute(orderId: String) = "order_complete/$orderId"
    }
}