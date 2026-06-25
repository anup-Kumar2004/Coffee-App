package com.example.coffeeapp.ui.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.coffeeapp.navigation.Screen

@Composable
fun SignUpScreenRoute(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        when (authState) {
            is AuthViewModel.AuthState.Success -> {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Welcome.route) { inclusive = true }
                }
                viewModel.resetState()
            }
            else -> Unit
        }
    }

    SignUpScreen(
        authState = authState,
        onSignUp = { email, password, firstName, lastName ->
            viewModel.signUp(email, password, firstName, lastName)
        },
        onNavigateBack = { navController.navigateUp() }
    )
}