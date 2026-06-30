package com.example.coffeeapp.ui.main

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.width
import com.example.coffeeapp.ui.theme.StarbucksGreen
import com.example.coffeeapp.ui.theme.StarbucksGray
import com.example.coffeeapp.ui.theme.StarbucksWhite



private val CartBadgeRed = Color(0xFFE0483E)

enum class BottomNavTab(
    val route: String,
    val label: String,
    val filledIcon: ImageVector,
    val outlinedIcon: ImageVector
) {
    HOME(
        route = "home",
        label = "Home",
        filledIcon = Icons.Filled.Home,
        outlinedIcon = Icons.Outlined.Home
    ),
    CART(
        route = "cart",
        label = "Cart",
        filledIcon = Icons.Filled.ShoppingBag,
        outlinedIcon = Icons.Outlined.ShoppingBag
    ),
    ORDER_HISTORY(
        route = "order_history",
        label = "Orders",
        filledIcon = Icons.Filled.History,
        outlinedIcon = Icons.Outlined.History
    ),
    PROFILE(
        route = "profile",
        label = "Profile",
        filledIcon = Icons.Filled.Person,
        outlinedIcon = Icons.Outlined.Person
    )
}

@Composable
fun BottomNavBar(
    selectedTab: BottomNavTab,
    cartItemCount: Int,
    hasActiveOrder: Boolean,
    onTabSelected: (BottomNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 12.dp, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
        color = StarbucksWhite,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(72.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavTab.entries.forEach { tab ->
                NavBarItem(
                    tab = tab,
                    isSelected = tab == selectedTab,
                    badgeCount = if (tab == BottomNavTab.CART) cartItemCount else 0,
                    showDotBadge = tab == BottomNavTab.ORDER_HISTORY && hasActiveOrder,
                    onClick = { onTabSelected(tab) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun NavBarItem(
    tab: BottomNavTab,
    isSelected: Boolean,
    badgeCount: Int,
    showDotBadge: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconColor = if (isSelected) StarbucksGreen else StarbucksGray
    val iconScale by animateFloatAsState(
        targetValue = if (isSelected) 1.08f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "navIconScale_${tab.name}"
    )

    Column(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box {
            Icon(
                imageVector = if (isSelected) tab.filledIcon else tab.outlinedIcon,
                contentDescription = tab.label,
                tint = iconColor,
                modifier = Modifier
                    .size(24.dp)
                    .graphicsLayer { scaleX = iconScale; scaleY = iconScale }
            )

            if (badgeCount > 0) {
                CartBadge(
                    count = badgeCount,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 6.dp, y = (-4).dp)
                )
            } else if (showDotBadge) {
                NotificationDot(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 4.dp, y = (-2).dp)
                )
            }
        }

        Text(
            text = tab.label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = iconColor
        )
    }
}

@Composable
private fun CartBadge(count: Int, modifier: Modifier = Modifier) {
    val displayText = if (count > 9) "9+" else count.toString()
    val isDoubleDigit = displayText.length > 1

    Box(
        modifier = modifier
            .then(
                if (isDoubleDigit)
                    Modifier
                        .width(18.dp)
                        .height(13.dp)
                else
                    Modifier.size(13.dp)
            )
            .background(CartBadgeRed, CircleShape)
            .padding(horizontal = if (isDoubleDigit) 2.dp else 0.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = displayText,
            color = Color.White,
            fontSize = 7.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            lineHeight = 7.sp
        )
    }
}

@Composable
private fun NotificationDot(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(10.dp)
            .background(StarbucksWhite, CircleShape)
            .padding(1.5.dp)
            .background(CartBadgeRed, CircleShape)
    )
}