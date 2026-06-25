package com.example.coffeeapp.ui.profile

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.coffeeapp.ui.components.ComingSoonPlaceholder


@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    ComingSoonPlaceholder(
        icon = Icons.Outlined.Person,
        title = "Profile",
        message = "Account details and settings are coming soon.",
        modifier = modifier
    )
}