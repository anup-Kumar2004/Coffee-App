package com.example.coffeeapp.ui.checkout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TimerOff
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.coffeeapp.model.Order
import com.example.coffeeapp.model.OrderItem
import com.example.coffeeapp.ui.theme.StarbucksBlack
import com.example.coffeeapp.ui.theme.StarbucksGold
import com.example.coffeeapp.ui.theme.StarbucksGray
import com.example.coffeeapp.ui.theme.StarbucksGreen
import com.example.coffeeapp.ui.theme.StarbucksMint
import com.example.coffeeapp.ui.theme.StarbucksWhite

private val CancelRed = Color(0xFFE0483E)

@Composable
fun OrderPickupScreen(
    uiState: OrderPickupViewModel.OrderPickupUiState,
    cancelState: OrderPickupViewModel.CancelState,
    expiryState: OrderPickupViewModel.ExpiryState,
    onToggleOtp: () -> Unit,
    onNavigateBack: () -> Unit,
    onCancelTapped: () -> Unit,
    onCancelDismissed: () -> Unit,
    onCancelConfirmed: () -> Unit,
    onCancelErrorDismissed: () -> Unit
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
                    onNavigateBack = onNavigateBack,
                    onCancelTapped = onCancelTapped
                )
            }
        }

        when (cancelState) {
            is OrderPickupViewModel.CancelState.Confirming -> {
                CancelConfirmationOverlay(
                    onKeepOrder = onCancelDismissed,
                    onConfirmCancel = onCancelConfirmed
                )
            }
            is OrderPickupViewModel.CancelState.Cancelling -> {
                CancelProgressOverlay(isDone = false)
            }
            is OrderPickupViewModel.CancelState.Cancelled -> {
                CancelProgressOverlay(isDone = true)
            }
            is OrderPickupViewModel.CancelState.Error -> {
                CancelErrorOverlay(
                    message = cancelState.message,
                    onDismiss = onCancelErrorDismissed
                )
            }
            else -> Unit
        }

        when (expiryState) {
            is OrderPickupViewModel.ExpiryState.Expiring -> {
                ExpiryOverlay(isDone = false)
            }
            is OrderPickupViewModel.ExpiryState.Expired -> {
                ExpiryOverlay(isDone = true)
            }
            else -> Unit
        }
    }
}

@Composable
private fun OrderPickupContent(
    state: OrderPickupViewModel.OrderPickupUiState.Success,
    onToggleOtp: () -> Unit,
    onNavigateBack: () -> Unit,
    onCancelTapped: () -> Unit
) {
    val order = state.order

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 40.dp)
    ) {
        // ── Top bar ──────────────────────────────────────────────────────────
        // FIX: title/order-id column now takes weight(1f) so it shrinks and
        // ellipsizes instead of pushing the status chip out of view when the
        // Firestore-generated order ID is long.
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
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Order Pickup",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = StarbucksBlack,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Order #${order.orderId}",
                    fontSize = 11.sp,
                    color = StarbucksGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            StatusChip(status = order.status)
        }

        Spacer(modifier = Modifier.height(8.dp))

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

        OtpCard(
            otp = order.otp,
            isRevealed = state.isOtpRevealed,
            onToggleOtp = onToggleOtp,
            remainingSeconds = state.remainingSeconds
        )

        Spacer(modifier = Modifier.height(16.dp))

        OrderSummaryCard(order = order)

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .background(StarbucksMint.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "This is a cash on pickup order. Please bring exact change to the store.",
                fontSize = 12.sp,
                color = StarbucksGreen,
                lineHeight = 18.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))


        AnimatedVisibility(
            visible = state.cancelButtonVisible,
            enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { it / 2 },
            exit = fadeOut(tween(300))
        ) {
            CancelOrderSection(
                enabled = state.cancelButtonEnabled,
                onCancelTapped = onCancelTapped
            )
        }
    }
}

@Composable
private fun CancelOrderSection(
    enabled: Boolean,
    onCancelTapped: () -> Unit
) {

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
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    enabled = enabled,
                    onClick = onCancelTapped
                )
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(CancelRed.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Cancel,
                    contentDescription = null,
                    tint = CancelRed,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Cancel this order",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = StarbucksBlack
                )
                Text(
                    text = "Free cancellation available right now",
                    fontSize = 11.5.sp,
                    lineHeight = 15.sp,
                    color = StarbucksGray
                )
            }
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
            color = textColor,
            maxLines = 1
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
                    text = "x ${item.quantity}",
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
private fun CancelConfirmationOverlay(
    onKeepOrder: () -> Unit,
    onConfirmCancel: () -> Unit
) {
    Dialog(
        onDismissRequest = onKeepOrder,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.65f)),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = StarbucksWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        CancelRed.copy(alpha = 0.10f),
                                        Color.Transparent
                                    )
                                )
                            )
                            .padding(top = 28.dp, bottom = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .background(CancelRed.copy(alpha = 0.12f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(CancelRed, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Cancel,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Cancel this order?",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            color = StarbucksBlack
                        )

                        Text(
                            text = "This can't be undone. Your order will be removed and no payment will be taken.",
                            fontSize = 13.sp,
                            color = StarbucksGray,
                            textAlign = TextAlign.Center,
                            lineHeight = 19.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // FIX: buttons stacked full-width vertically instead of a
                        // weighted Row. The previous side-by-side layout clipped
                        // "Keep order" / "Cancel order" at bold 14sp on narrower
                        // screens. Stacking guarantees single-line text regardless
                        // of font size, matching the pattern used by iOS action
                        // sheets and most ride/delivery app cancellation dialogs.
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = onConfirmCancel,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CancelRed)
                            ) {
                                Text(
                                    text = "Cancel order",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            }

                            Button(
                                onClick = onKeepOrder,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = StarbucksGreen
                                ),
                                border = BorderStroke(1.dp, StarbucksGreen.copy(alpha = 0.35f)),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                            ) {
                                Text(
                                    text = "Keep order",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CancelProgressOverlay(isDone: Boolean) {
    FullScreenScrimOverlay {
        if (!isDone) {
            CircularProgressIndicator(
                color = CancelRed,
                modifier = Modifier.size(52.dp),
                strokeWidth = 3.dp
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Cancelling your order",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = StarbucksBlack
            )
            Spacer(modifier = Modifier.height(8.dp))
            AnimatedEllipsisText(
                text = "Please wait",
                color = StarbucksGray
            )
        } else {
            OverlayResultIcon(
                icon = Icons.Default.CheckCircle,
                tint = CancelRed,
                backgroundTint = Color(0xFFFFEDEC)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Order Cancelled",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = StarbucksBlack
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Redirecting you now",
                fontSize = 14.sp,
                color = StarbucksGray
            )
        }
    }
}

@Composable
private fun ExpiryOverlay(isDone: Boolean) {
    FullScreenScrimOverlay {
        if (!isDone) {
            CircularProgressIndicator(
                color = StarbucksGray,
                modifier = Modifier.size(52.dp),
                strokeWidth = 3.dp
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Order window closed",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = StarbucksBlack
            )
            Spacer(modifier = Modifier.height(8.dp))
            AnimatedEllipsisText(
                text = "Please wait",
                color = StarbucksGray
            )
        } else {
            OverlayResultIcon(
                icon = Icons.Default.TimerOff,
                tint = StarbucksGray,
                backgroundTint = Color(0xFFF0F0F0)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Order Expired",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = StarbucksBlack
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Redirecting you now",
                fontSize = 14.sp,
                color = StarbucksGray
            )
        }
    }
}

@Composable
private fun CancelErrorOverlay(message: String, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f)),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = StarbucksWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Could not cancel",
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
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = StarbucksGreen)
                    ) {
                        Text(text = "OK", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun FullScreenScrimOverlay(content: @Composable ColumnScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = StarbucksWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                content = content
            )
        }
    }
}

@Composable
private fun OverlayResultIcon(
    icon: ImageVector,
    tint: Color,
    backgroundTint: Color
) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .background(backgroundTint, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(40.dp)
        )
    }
}

@Composable
private fun AnimatedEllipsisText(text: String, color: Color) {
    val transition = rememberInfiniteTransition(label = "ellipsis")
    val dotCount by transition.animateFloat(
        initialValue = 0f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Restart
        ),
        label = "dotCount"
    )
    val dots = ".".repeat(dotCount.toInt())
    Text(
        text = "$text$dots",
        fontSize = 14.sp,
        color = color
    )
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