package com.example.coffeeapp.ui.orderhistory

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.coffeeapp.ui.components.ComingSoonPlaceholder


@Composable
fun OrderHistoryScreen(modifier: Modifier = Modifier) {
    ComingSoonPlaceholder(
        icon = Icons.Outlined.History,
        title = "No orders yet",
        message = "Your past and active orders will appear here once you place one.",
        modifier = modifier
    )
}