package com.example.coffeeapp.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.coffeeapp.ui.cart.CartScreenRoute
import com.example.coffeeapp.ui.cart.CartViewModel
import com.example.coffeeapp.ui.home.HomeScreenRoute
import com.example.coffeeapp.ui.orderhistory.OrderHistoryScreen
import com.example.coffeeapp.ui.profile.ProfileScreen

@Composable
fun MainScreen(
    outerNavController: NavController,
    modifier: Modifier = Modifier
) {
    val innerNavController = rememberNavController()
    val cartViewModel: CartViewModel = hiltViewModel()
    val cartItemCount by cartViewModel.cartItems.collectAsState()
    val totalCartCount = cartItemCount.size

    val mainViewModel: MainViewModel = hiltViewModel()
    val hasActiveOrder by mainViewModel.hasActiveOrder.collectAsState()

    val currentBackStackEntry by innerNavController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val selectedTab = BottomNavTab.entries.find { it.route == currentRoute } ?: BottomNavTab.HOME

    Scaffold(
        modifier = modifier,
        bottomBar = {
            BottomNavBar(
                selectedTab = selectedTab,
                cartItemCount = totalCartCount,
                hasActiveOrder = hasActiveOrder,
                onTabSelected = { tab ->
                    if (tab.route != currentRoute) {
                        innerNavController.navigate(tab.route) {
                            popUpTo(innerNavController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = innerNavController,
            startDestination = BottomNavTab.HOME.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavTab.HOME.route) {
                HomeScreenRoute(navController = outerNavController)
            }
            composable(BottomNavTab.CART.route) {
                CartScreenRoute(navController = outerNavController)
            }
            composable(BottomNavTab.ORDER_HISTORY.route) {
                OrderHistoryScreen()
            }
            composable(BottomNavTab.PROFILE.route) {
                ProfileScreen()
            }
        }
    }
}