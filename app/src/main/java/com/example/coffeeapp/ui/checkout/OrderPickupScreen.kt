package com.example.coffeeapp.ui.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WifiOff
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coffeeapp.model.Order
import com.example.coffeeapp.model.OrderItem
import com.example.coffeeapp.ui.theme.StarbucksBlack
import com.example.coffeeapp.ui.theme.StarbucksGold
import com.example.coffeeapp.ui.theme.StarbucksGray
import com.example.coffeeapp.ui.theme.StarbucksGreen
import com.example.coffeeapp.ui.theme.StarbucksMint
import com.example.coffeeapp.ui.theme.StarbucksWhite

@Composable
fun OrderPickupScreen(
    uiState: OrderPickupViewModel.OrderPickupUiState,
    onToggleOtp: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(StarbucksWhite)
    ) {
        when (uiState) {
            is OrderPickupViewModel.OrderPickupUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = StarbucksGreen)
                }
            }
            is OrderPickupViewModel.OrderPickupUiState.Error -> {
                OrderPickupErrorState(message = uiState.message, onNavigateBack = onNavigateBack)
            }
            is OrderPickupViewModel.OrderPickupUiState.Success -> {
                OrderPickupContent(
                    state = uiState,
                    onToggleOtp = onToggleOtp,
                    onNavigateBack = onNavigateBack
                )
            }
        }
    }
}

@Composable
private fun OrderPickupContent(
    state: OrderPickupViewModel.OrderPickupUiState.Success,
    onToggleOtp: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val order = state.order

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 40.dp)
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
                    text = "Order Pickup",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = StarbucksBlack
                )
                Text(
                    text = "Order #${order.orderId.takeLast(6).uppercase()}",
                    fontSize = 12.sp,
                    color = StarbucksGray
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            StatusChip(status = order.status)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Store info card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(StarbucksMint, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Storefront,
                        contentDescription = null,
                        tint = StarbucksGreen
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = order.storeName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = StarbucksBlack
                    )
                    Text(
                        text = order.storeAddress,
                        fontSize = 12.sp,
                        color = StarbucksGray,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // OTP Card
        OtpCard(
            otp = order.otp,
            isRevealed = state.isOtpRevealed,
            onToggleOtp = onToggleOtp,
            remainingSeconds = state.remainingSeconds
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Order summary card
        OrderSummaryCard(order = order)

        Spacer(modifier = Modifier.height(16.dp))

        // Cash on pickup note
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .background(StarbucksMint.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
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
}

@Composable
private fun StatusChip(status: String) {
    val (bgColor, textColor, label) = when (status.lowercase()) {
        "pending" -> Triple(Color(0xFFFFF3CD), Color(0xFF8A6D00), "Pending")
        "completed" -> Triple(Color(0xFFD9F2E3), StarbucksGreen, "Completed")
        else -> Triple(StarbucksMint, StarbucksGreen, status.replaceFirstChar { it.uppercase() })
    }
    Box(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(50.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
private fun OtpCard(
    otp: String,
    isRevealed: Boolean,
    onToggleOtp: () -> Unit,
    remainingSeconds: Long
) {
    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val timerText = if (remainingSeconds > 0) {
        "Order valid for %02d:%02d".format(minutes, seconds)
    } else {
        "This order has expired"
    }

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
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Your Pickup OTP",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = StarbucksBlack
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (isRevealed) otp.toCharArray().joinToString("  ") else "●  ●  ●  ●  ●  ●",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = StarbucksGreen,
                letterSpacing = 4.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onToggleOtp,
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StarbucksMint),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Icon(
                    imageVector = if (isRevealed) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = null,
                    tint = StarbucksGreen,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isRevealed) "Hide OTP" else "Reveal OTP",
                    color = StarbucksGreen,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Only show your OTP to the store staff when you are inside the store. For security, do not share it with anyone else.",
                fontSize = 11.sp,
                color = StarbucksGray,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = timerText,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (remainingSeconds > 0) StarbucksGold else Color(0xFFE0483E)
            )
        }
    }
}

@Composable
private fun OrderSummaryCard(order: Order) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Order Summary",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = StarbucksBlack
            )

            Spacer(modifier = Modifier.height(12.dp))

            order.items.forEachIndexed { index, item ->
                OrderPickupItemRow(item = item)
                if (index < order.items.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        color = Color(0xFFF0F0F0),
                        thickness = 1.dp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Grand Total",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = StarbucksBlack
                )
                Text(
                    text = "$" + "%.2f".format(order.grandTotal),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = StarbucksGreen
                )
            }
        }
    }
}

@Composable
private fun OrderPickupItemRow(item: OrderItem) {
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
            text = "$" + "%.2f".format(item.lineTotal),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = StarbucksBlack
        )
    }
}

@Composable
private fun OrderPickupErrorState(message: String, onNavigateBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.WifiOff,
                contentDescription = null,
                tint = StarbucksGray,
                modifier = Modifier.size(64.dp)
            )
            Text(
                text = "Something went wrong",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = StarbucksBlack
            )
            Text(
                text = message,
                fontSize = 14.sp,
                color = StarbucksGray,
                textAlign = TextAlign.Center
            )
            Button(
                onClick = onNavigateBack,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StarbucksGreen)
            ) {
                Text(text = "Go Back", fontSize = 15.sp)
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F8F7)
@Composable
fun OrderPickupScreenPreview() {
    val fakeOrder = Order(
        orderId = "abc123XYZ456",
        userId = "user1",
        items = listOf(
            OrderItem(
                productId = "1", productName = "Caramel Latte", selectedSize = "Medium",
                quantity = 2, unitPrice = 320.0, lineTotal = 640.0
            ),
            OrderItem(
                productId = "2", productName = "Cold Brew", selectedSize = "Large",
                quantity = 1, unitPrice = 350.0, lineTotal = 350.0
            )
        ),
        itemsTotal = 990.0,
        serviceFee = 0.50,
        tax = 49.5,
        grandTotal = 1040.0,
        storeId = "store1",
        storeName = "Crema, Sector 6",
        storeAddress = "Shop 4, Sector 6 Market, Gurugram, Haryana 122001",
        otp = "482913",
        status = "pending"
    )
    OrderPickupScreen(
        uiState = OrderPickupViewModel.OrderPickupUiState.Success(
            order = fakeOrder,
            isOtpRevealed = false,
            remainingSeconds = 1247
        ),
        onToggleOtp = {},
        onNavigateBack = {}
    )
}