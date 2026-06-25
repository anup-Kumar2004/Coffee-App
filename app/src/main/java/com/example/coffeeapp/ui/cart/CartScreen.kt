package com.example.coffeeapp.ui.cart

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.coffeeapp.ui.components.ComingSoonPlaceholder


@Composable
fun CartScreen(modifier: Modifier = Modifier) {
    ComingSoonPlaceholder(
        icon = Icons.Outlined.ShoppingBag,
        title = "Your cart is empty",
        message = "Items you add will show up here. Cart logic is coming in a future phase.",
        modifier = modifier
    )
}