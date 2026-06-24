package com.example.coffeeapp.ui.main

import androidx.lifecycle.ViewModel
import com.example.coffeeapp.data.AuthRepository
import com.example.coffeeapp.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    authRepository: AuthRepository
) : ViewModel() {

    private val _startDestination = MutableStateFlow(
        if (authRepository.getCurrentUser() != null) {
            Screen.Home.route
        } else {
            Screen.Welcome.route
        }
    )
    val startDestination = _startDestination.asStateFlow()
}