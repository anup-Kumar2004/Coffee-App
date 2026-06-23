package com.example.coffeeapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.coffeeapp.ui.welcome.WelcomeScreen

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object Home : Screen("home")
    object ProductDetail : Screen("product_detail")
    object Cart : Screen("cart")
    object Favorites : Screen("favorites")
    object Profile : Screen("profile")
}

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route
    ) {
        composable(Screen.Welcome.route) { WelcomeScreen() }
        composable(Screen.Login.route) { }
        composable(Screen.SignUp.route) { }
        composable(Screen.Home.route) { }
        composable(Screen.ProductDetail.route) { }
        composable(Screen.Cart.route) { }
        composable(Screen.Favorites.route) { }
        composable(Screen.Profile.route) { }
    }
}