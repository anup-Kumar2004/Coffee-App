package com.example.coffeeapp.ui.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coffeeapp.model.CartItem
import com.example.coffeeapp.ui.theme.StarbucksBlack
import com.example.coffeeapp.ui.theme.StarbucksGold
import com.example.coffeeapp.ui.theme.StarbucksGray
import com.example.coffeeapp.ui.theme.StarbucksGreen
import com.example.coffeeapp.ui.theme.StarbucksMint
import com.example.coffeeapp.ui.theme.StarbucksWhite

@Composable
fun OrderSummaryScreen(
    uiState: OrderSummaryViewModel.OrderSummaryUiState,
    onNavigateBack: () -> Unit,
    onProceedToStoreLocator: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(StarbucksWhite)
    ) {
        when (uiState) {
            is OrderSummaryViewModel.OrderSummaryUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = StarbucksGreen)
                }
            }

            is OrderSummaryViewModel.OrderSummaryUiState.Success -> {
                OrderSummaryContent(
                    state = uiState,
                    onNavigateBack = onNavigateBack,
                    onProceedToStoreLocator = onProceedToStoreLocator
                )
            }
        }
    }
}

@Composable
private fun OrderSummaryContent(
    state: OrderSummaryViewModel.OrderSummaryUiState.Success,
    onNavigateBack: () -> Unit,
    onProceedToStoreLocator: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 100.dp)
        ) {
            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(40.dp)
                        .background(StarbucksMint, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = StarbucksGreen
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Order Summary",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = StarbucksBlack
                    )
                    Text(
                        text = "Review your order before proceeding",
                        fontSize = 12.sp,
                        color = StarbucksGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Items card
            var itemsExpanded by remember { mutableStateOf(false) }
            val visibleItems = if (itemsExpanded) state.cartItems else state.cartItems.take(3)
            val hasMore = state.cartItems.size > 3
            val hiddenCount = state.cartItems.size - 3

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Your Items",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = StarbucksBlack
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    visibleItems.forEachIndexed { index, item ->
                        OrderItemRow(item = item)
                        if (index < visibleItems.lastIndex || (hasMore && !itemsExpanded)) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 10.dp),
                                color = Color(0xFFF0F0F0),
                                thickness = 1.dp
                            )
                        }
                    }

                    if (hasMore) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = { itemsExpanded = !itemsExpanded }
                                )
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = if (itemsExpanded) "Show less" else "Show $hiddenCount more ${if (hiddenCount == 1) "item" else "items"}",
                                    color = StarbucksGreen,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = if (itemsExpanded) "▲" else "▼",
                                    color = StarbucksGreen,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Price breakdown card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Price Breakdown",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = StarbucksBlack
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    PriceRow(
                        label = "Items Total",
                        value = "$" + "%.2f".format(state.itemsTotal),
                        labelColor = StarbucksGray,
                        valueColor = StarbucksBlack
                    )

                    PriceRow(
                        label = "Crema Service Fee",
                        value = "$" + "%.2f".format(state.serviceFee),
                        labelColor = StarbucksGold,
                        valueColor = StarbucksGold,
                        labelFontStyle = FontStyle.Italic
                    )

                    PriceRow(
                        label = "Taxes (5%)",
                        value = "$" + "%.2f".format(state.tax),
                        labelColor = StarbucksGray,
                        valueColor = StarbucksBlack
                    )

                    HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Grand Total",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = StarbucksBlack
                        )
                        Text(
                            text = "$" + "%.2f".format(state.grandTotal),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = StarbucksGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Info note
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(
                        StarbucksMint.copy(alpha = 0.4f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "ℹ️", fontSize = 16.sp)
                Text(
                    text = "This is a cash on pickup order. Please bring exact change to the store.",
                    fontSize = 12.sp,
                    color = StarbucksGreen,
                    lineHeight = 18.sp
                )
            }
        }

        // Bottom CTA
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, StarbucksWhite, StarbucksWhite)
                    )
                )
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Button(
                onClick = onProceedToStoreLocator,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StarbucksGreen)
            ) {
                Text(
                    text = "Find My Nearest Store →",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun OrderItemRow(item: CartItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.productName,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = StarbucksBlack
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Size pill
                Box(
                    modifier = Modifier
                        .background(StarbucksMint, RoundedCornerShape(50.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = item.selectedSize.replaceFirstChar { it.uppercase() },
                        fontSize = 10.sp,
                        color = StarbucksGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text(
                    text = "× ${item.quantity}",
                    fontSize = 12.sp,
                    color = StarbucksGray
                )
            }
        }

        Text(
            text = "$" + "%.2f".format(item.unitPrice * item.quantity),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = StarbucksBlack
        )
    }
}

@Composable
private fun PriceRow(
    label: String,
    value: String,
    labelColor: Color,
    valueColor: Color,
    labelFontStyle: FontStyle = FontStyle.Normal
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = labelColor,
            fontStyle = labelFontStyle
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = valueColor
        )
    }
}