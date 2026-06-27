package com.example.coffeeapp.ui.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.coffeeapp.model.CartItem
import com.example.coffeeapp.ui.theme.*

@Composable
fun CartScreen(modifier: Modifier = Modifier) {
    val viewModel: CartViewModel = hiltViewModel()
    val cartItems by viewModel.cartItems.collectAsState()
    val totalPrice by viewModel.totalPrice.collectAsState()
    var showClearDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(StarbucksSurface)
    ) {
        if (cartItems.isEmpty()) {
            CartEmptyState()
        } else {
            CartContent(
                cartItems = cartItems,
                totalPrice = totalPrice,
                onIncrement = viewModel::incrementQuantity,
                onDecrement = viewModel::decrementQuantity,
                onRemove = viewModel::removeItem,
                onClearCart = { showClearDialog = true }
            )
        }
    }

    if (showClearDialog) {
        ClearCartDialog(
            onConfirm = {
                viewModel.clearCart()
                showClearDialog = false
            },
            onDismiss = { showClearDialog = false }
        )
    }
}

@Composable
private fun CartEmptyState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(40.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(StarbucksMint, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.ShoppingBag,
                    contentDescription = null,
                    tint = StarbucksGreen,
                    modifier = Modifier.size(44.dp)
                )
            }
            Text(
                text = "Your cart is empty",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = StarbucksBlack
            )
            Text(
                text = "Add some items from the menu to get started.",
                fontSize = 14.sp,
                color = StarbucksGray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CartContent(
    cartItems: List<CartItem>,
    totalPrice: Double,
    onIncrement: (CartItem) -> Unit,
    onDecrement: (CartItem) -> Unit,
    onRemove: (CartItem) -> Unit,
    onClearCart: () -> Unit
) {

    Box(modifier = Modifier.fillMaxSize()) {

        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp, end = 16.dp, top = 20.dp, bottom = 165.dp
            ),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                        .padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Your Cart",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = StarbucksBlack
                        )
                        Text(
                            text = "${cartItems.size} ${if (cartItems.size == 1) "item" else "items"}",
                            fontSize = 13.sp,
                            color = StarbucksGray
                        )
                    }
                    // Clear all button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50.dp))
                            .background(Color(0xFFFFEDEC))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onClearCart
                            )
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Clear cart",
                                tint = Color(0xFFE0483E),
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = "Clear all",
                                color = Color(0xFFE0483E),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            items(cartItems, key = { "${it.productId}_${it.selectedSize}" }) { item ->
                CartItemCard(
                    item = item,
                    onIncrement = { onIncrement(item) },
                    onDecrement = { onDecrement(item) },
                    onRemove = { onRemove(item) }
                )
            }
        }

        // Checkout panel with a top fade gradient so list fades into it
        // naturally — makes it obvious more items exist below.
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            // Fade gradient — transparent → white, 32dp tall
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, StarbucksWhite)
                        )
                    )
            )

            // Checkout panel
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 16.dp, bottom = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Order summary row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "Order Total",
                                fontSize = 12.sp,
                                color = StarbucksGray,
                                fontWeight = FontWeight.Normal
                            )
                            Text(
                                text = "$" + "%.2f".format(totalPrice),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = StarbucksBlack
                            )
                        }
                        Column(
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "incl. taxes & restaurant charges",
                                fontSize = 10.sp,
                                color = StarbucksGray
                            )
                            Text(
                                text = "${cartItems.size} ${if (cartItems.size == 1) "product" else "products"}",
                                fontSize = 12.sp,
                                color = StarbucksGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Button(
                        onClick = { /* Phase 10 */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = StarbucksGreen)
                    ) {
                        Text(
                            text = "Proceed to Checkout",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CartItemCard(
    item: CartItem,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onRemove: () -> Unit
) {
    val bgColor = remember(item.cardColor) {
        try { Color(item.cardColor.toColorInt()) } catch (_: Exception) { Color(0xFF966E58) }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Product image with brand color background
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(bgColor.copy(alpha = 0.6f), bgColor)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.productName,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(66.dp)
                        .padding(4.dp)
                )
            }

            // Middle: name, size pill, unit price
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = item.productName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = StarbucksBlack,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Size pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(StarbucksMint)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = item.selectedSize.replaceFirstChar { it.uppercase() },
                        fontSize = 10.sp,
                        color = StarbucksGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Unit price in gold — tells user what one item costs
                Text(
                    text = "$" + "%.2f".format(item.unitPrice) + " each",
                    fontSize = 11.sp,
                    color = StarbucksGold,
                    fontWeight = FontWeight.Medium
                )

                // Total for this line item
                Text(
                    text = "$" + "%.2f".format(item.unitPrice * item.quantity),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = StarbucksGreen
                )
            }

            // Right: stepper + remove
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(StarbucksMint)
                        .padding(3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SmallQtyButton(
                        icon = Icons.Default.Remove,
                        onClick = onDecrement,
                        enabled = true
                    )
                    Text(
                        text = item.quantity.toString(),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = StarbucksGreen,
                        modifier = Modifier.width(28.dp),
                        textAlign = TextAlign.Center
                    )
                    SmallQtyButton(
                        icon = Icons.Default.Add,
                        onClick = onIncrement,
                        enabled = item.quantity < 10
                    )
                }

                TextButton(
                    onClick = onRemove,
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.height(22.dp)
                ) {
                    Text(
                        text = "Remove",
                        color = Color(0xFFE0483E),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun SmallQtyButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    enabled: Boolean
) {
    Box(
        modifier = Modifier
            .size(26.dp)
            .clip(CircleShape)
            .background(if (enabled) StarbucksGreen else StarbucksGreen.copy(alpha = 0.35f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = enabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(13.dp)
        )
    }
}

@Composable
private fun ClearCartDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = StarbucksWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(Color(0xFFFFEDEC), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = null,
                        tint = Color(0xFFE0483E),
                        modifier = Modifier.size(32.dp)
                    )
                }
                Text(
                    text = "Clear Cart?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = StarbucksBlack
                )
                Text(
                    text = "This will remove all items from your cart. This action cannot be undone.",
                    fontSize = 14.sp,
                    color = StarbucksGray,
                    textAlign = TextAlign.Center
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, StarbucksGreen)
                    ) {
                        Text(
                            text = "Cancel",
                            color = StarbucksGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0483E))
                    ) {
                        Text(text = "Clear", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}