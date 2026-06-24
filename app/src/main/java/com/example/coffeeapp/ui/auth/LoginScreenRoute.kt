package com.example.coffeeapp.ui.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.coffeeapp.navigation.Screen


@Composable
fun LoginScreenRoute(
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

    LoginScreen(
        authState = authState,
        onSignIn = { email, password -> viewModel.signIn(email, password) },
        onNavigateToSignUp = {
            viewModel.resetState()
            navController.navigate(Screen.SignUp.route)
        },
        onNavigateBack = { navController.navigateUp() }
    )
}