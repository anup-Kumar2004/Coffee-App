package com.example.coffeeapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.coffeeapp.ui.auth.LoginScreenRoute
import com.example.coffeeapp.ui.auth.SignUpScreenRoute
import com.example.coffeeapp.ui.main.MainScreen
import com.example.coffeeapp.ui.welcome.WelcomeScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = { NavTransitions.enterTransition },
        exitTransition = { NavTransitions.exitTransition },
        popEnterTransition = { NavTransitions.popEnterTransition },
        popExitTransition = { NavTransitions.popExitTransition }
    ) {
        composable(Screen.Welcome.route) {
            WelcomeScreen(onGetStarted = { navController.navigate(Screen.Login.route) })
        }
        composable(Screen.Login.route) {
            LoginScreenRoute(navController = navController)
        }
        composable(Screen.SignUp.route) {
            SignUpScreenRoute(navController = navController)
        }
        composable(Screen.MainScreen.route) {
            MainScreen(outerNavController = navController)
        }
        composable(Screen.ProductDetail.route) { }
        // Phase 6: ProductDetail will receive a productId nav argument here.
        // Cart, OrderHistory, and Profile no longer have outer routes — they live
        // inside MainScreen's own inner NavHost as bottom-nav tabs.
    }
}