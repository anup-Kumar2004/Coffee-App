package com.example.coffeeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.coffeeapp.navigation.AppNavHost
import com.example.coffeeapp.ui.main.MainViewModel
import com.example.coffeeapp.ui.theme.CoffeeAppTheme
import dagger.hilt.android.AndroidEntryPoint



@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CoffeeAppTheme {
                val startDestination by mainViewModel.startDestination.collectAsState()
                val navController = rememberNavController()

                // No Scaffold here anymore — MainScreen (the bottom-nav tab container)
                // owns its own Scaffold for the bottom bar, which already accounts for the
                // bottom system nav bar correctly via its own innerPadding. Applying
                // statusBarsPadding()/navigationBarsPadding() HERE would double-pad
                // MainScreen's bottom bar (a visible empty gap below it).
                // Instead, each screen OUTSIDE MainScreen's tab structure (Welcome, Login,
                // SignUp, and later ProductDetail) applies statusBarsPadding() +
                // navigationBarsPadding() individually on its own root Modifier.
                AppNavHost(
                    navController = navController,
                    startDestination = startDestination,
                    modifier = Modifier
                        .fillMaxSize()
                        .imePadding()
                )
            }
        }
    }
}