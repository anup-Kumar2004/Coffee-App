package com.example.coffeeapp.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeapp.data.AuthRepository
import com.example.coffeeapp.data.OrderRepository
import com.example.coffeeapp.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    authRepository: AuthRepository,
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _startDestination = MutableStateFlow(
        if (authRepository.getCurrentUser() != null) {
            Screen.MainScreen.route
        } else {
            Screen.Welcome.route
        }
    )
    val startDestination = _startDestination.asStateFlow()

    private val _hasActiveOrder = MutableStateFlow(false)
    val hasActiveOrder = _hasActiveOrder.asStateFlow()

    init {
        viewModelScope.launch {
            orderRepository.listenToActivePendingOrder().collect { order ->
                _hasActiveOrder.value = order != null
            }
        }
    }
}