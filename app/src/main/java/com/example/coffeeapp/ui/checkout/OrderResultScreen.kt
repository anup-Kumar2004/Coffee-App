package com.example.coffeeapp.ui.checkout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TimerOff
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coffeeapp.model.Order
import com.example.coffeeapp.ui.theme.StarbucksBlack
import com.example.coffeeapp.ui.theme.StarbucksGray
import com.example.coffeeapp.ui.theme.StarbucksGreen
import com.example.coffeeapp.ui.theme.StarbucksMint
import com.example.coffeeapp.ui.theme.StarbucksSurface
import com.example.coffeeapp.ui.theme.StarbucksWhite
import java.text.SimpleDateFormat
import java.util.Locale

// ── Visual configuration resolved from the result type ─────────────────────────
// Kept as a private data class here rather than on the ViewModel so that color
// values (which are Compose-layer concerns) never leak into business logic.
private data class ResultVisuals(
    val accentColor: Color,
    val headerGradient: List<Color>,
    val icon: ImageVector,
    val headline: String,
    val subtext: String
)

private fun resolveVisuals(resultType: OrderResultType): ResultVisuals = when (resultType) {
    OrderResultType.CANCELLED -> ResultVisuals(
        accentColor = Color(0xFFF59E0B),
        headerGradient = listOf(Color(0xFFF59E0B), Color(0xFFD97706)),
        icon = Icons.Default.Cancel,
        headline = "Order Cancelled",
        subtext = "Your order was cancelled. No payment was taken."
    )
    OrderResultType.EXPIRED -> ResultVisuals(
        accentColor = StarbucksGray,
        headerGradient = listOf(StarbucksGray, Color(0xFF7A8A7C)),
        icon = Icons.Default.TimerOff,
        headline = "Order Expired",
        subtext = "Your order window closed. Please place a new order when you're ready."
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Public entry point — pure UI, no ViewModel references.
// Mirrors the top-level dispatcher pattern in OrderCompleteScreen.kt.
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun OrderResultScreen(
    uiState: OrderResultViewModel.OrderResultUiState,
    resultType: OrderResultType,
    onBackToHome: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(StarbucksWhite)
    ) {
        when (uiState) {
            is OrderResultViewModel.OrderResultUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = StarbucksGreen)
                }
            }
            is OrderResultViewModel.OrderResultUiState.Error -> {
                OrderResultErrorState(
                    message = uiState.message,
                    onBackToHome = onBackToHome
                )
            }
            is OrderResultViewModel.OrderResultUiState.Success -> {
                OrderResultContent(
                    order = uiState.order,
                    resultType = resultType,
                    onBackToHome = onBackToHome
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Success path — all cards animate in once on first composition.
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun OrderResultContent(
    order: Order,
    resultType: OrderResultType,
    onBackToHome: () -> Unit
) {
    val visuals = remember(resultType) { resolveVisuals(resultType) }
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 100.dp)
        ) {
            // Header — full-width coloured band with icon + headline
            ResultHeader(visuals = visuals)

            Spacer(modifier = Modifier.height(20.dp))

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 3 })
            ) {
                ResultOrderIdCard(order = order, accentColor = visuals.accentColor)
            }

            Spacer(modifier = Modifier.height(12.dp))

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 3 })
            ) {
                ResultStoreCard(order = order)
            }

            Spacer(modifier = Modifier.height(12.dp))

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 3 })
            ) {
                ResultSummaryCard(order = order, accentColor = visuals.accentColor)
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        // Sticky "Back to Home" CTA — same gradient scrim pattern as OrderCompleteScreen
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
                onClick = onBackToHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StarbucksGreen)
            ) {
                Text(
                    text = "Back to Home",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Header band — accent-coloured gradient, bouncy icon scale entrance.
// statusBarsPadding() is applied here so the coloured band bleeds into the
// status bar area, matching the OrderCompleteScreen's SuccessHeader treatment.
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ResultHeader(visuals: ResultVisuals) {
    // Starts at 0 so the icon "pops" in with a spring on first composition
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "resultIconScale"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(colors = visuals.headerGradient)
            )
            .statusBarsPadding()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = visuals.icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .size(72.dp)
                .graphicsLayer { scaleX = scale; scaleY = scale }
        )
        Text(
            text = visuals.headline,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = visuals.subtext,
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.85f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp),
            lineHeight = 20.sp
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Order ID card with copy-to-clipboard button.
// Mirrors the OrderIdCard in OrderCompleteScreen.kt exactly — same layout,
// same clipboard API calls, accent color swapped per result type.
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ResultOrderIdCard(order: Order, accentColor: Color) {
    val context = LocalContext.current
    val formattedDate = remember(order.timestamp) {
        try {
            val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            sdf.format(order.timestamp.toDate())
        } catch (_: Exception) { "—" }
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(
                        text = "Order ID",
                        fontSize = 11.sp,
                        color = StarbucksGray,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = order.orderId,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor,
                        letterSpacing = 0.5.sp
                    )
                }

                // Copy-to-clipboard — same clipboard API pattern as OrderCompleteScreen
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(StarbucksMint, CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
                                val clipboard = context.getSystemService(
                                    android.content.Context.CLIPBOARD_SERVICE
                                ) as android.content.ClipboardManager
                                val clip = android.content.ClipData.newPlainText(
                                    "Order ID", order.orderId
                                )
                                clipboard.setPrimaryClip(clip)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy Order ID",
                        tint = StarbucksGreen,
                        modifier = Modifier.size(17.dp)
                    )
                }
            }

            HorizontalDivider(color = StarbucksSurface, thickness = 1.dp)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = StarbucksGray,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = "Placed at",
                    fontSize = 12.sp,
                    color = StarbucksGray
                )
                Text(
                    text = formattedDate,
                    fontSize = 12.sp,
                    color = StarbucksBlack,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Store info — mirrors StoreInfoCard in OrderCompleteScreen.kt.
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ResultStoreCard(order: Order) {
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
                    .size(44.dp)
                    .background(StarbucksMint, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Storefront,
                    contentDescription = null,
                    tint = StarbucksGreen,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "Selected store",
                    fontSize = 11.sp,
                    color = StarbucksGray
                )
                Text(
                    text = order.storeName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = StarbucksBlack
                )
                Text(
                    text = order.storeAddress,
                    fontSize = 11.sp,
                    color = StarbucksGray,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Grand total summary — a lightweight card showing just the total for reference.
// We deliberately keep this lighter than the full ReceiptCard in
// OrderCompleteScreen: the user doesn't need itemised line items for a failed
// order, but they do need a monetary reference for any follow-up queries.
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ResultSummaryCard(order: Order, accentColor: Color) {
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
                text = "Order Summary",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = StarbucksBlack
            )

            HorizontalDivider(color = StarbucksSurface, thickness = 1.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${order.items.sumOf { it.quantity }} item(s)",
                    fontSize = 13.sp,
                    color = StarbucksGray
                )
                Text(
                    text = "$" + "%.2f".format(order.grandTotal),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
            }

            // Reassurance note — important for cancelled orders so the user
            // knows with certainty that no charge was processed.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(StarbucksMint.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "No payment was taken — this was a cash on pickup order.",
                    fontSize = 12.sp,
                    color = StarbucksGreen,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 17.sp
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Error fallback — shown when the single getOrderById() call fails.
// Mirrors OrderCompleteErrorState structure exactly.
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun OrderResultErrorState(message: String, onBackToHome: () -> Unit) {
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
                text = "Couldn't load order",
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
                onClick = onBackToHome,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StarbucksGreen)
            ) {
                Text(text = "Back to Home", fontSize = 15.sp)
            }
        }
    }
}