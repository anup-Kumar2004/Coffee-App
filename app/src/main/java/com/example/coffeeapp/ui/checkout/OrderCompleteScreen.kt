package com.example.coffeeapp.ui.checkout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coffeeapp.model.Order
import com.example.coffeeapp.model.OrderItem
import com.example.coffeeapp.ui.theme.StarbucksBlack
import com.example.coffeeapp.ui.theme.StarbucksGold
import com.example.coffeeapp.ui.theme.StarbucksGray
import com.example.coffeeapp.ui.theme.StarbucksGreen
import com.example.coffeeapp.ui.theme.StarbucksMint
import com.example.coffeeapp.ui.theme.StarbucksSurface
import com.example.coffeeapp.ui.theme.StarbucksWhite
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.ui.platform.LocalContext
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.platform.LocalDensity


@Composable
fun OrderCompleteScreen(
    uiState: OrderCompleteViewModel.OrderCompleteUiState,
    productRatings: Map<String, Int>,
    ratingState: OrderCompleteViewModel.RatingState,
    ticketState: OrderCompleteViewModel.TicketState,
    onProductRatingChanged: (productId: String, stars: Int) -> Unit,
    onSubmitRatings: () -> Unit,
    onResetRatingState: () -> Unit,
    onSubmitTicket: (issueType: String, description: String) -> Unit,
    onBackToHome: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(StarbucksWhite)
    ) {
        when (uiState) {
            is OrderCompleteViewModel.OrderCompleteUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = StarbucksGreen)
                }
            }
            is OrderCompleteViewModel.OrderCompleteUiState.Error -> {
                OrderCompleteErrorState(
                    message = uiState.message,
                    onBackToHome = onBackToHome
                )
            }
            is OrderCompleteViewModel.OrderCompleteUiState.Success -> {
                OrderCompleteContent(
                    order = uiState.order,
                    productRatings = productRatings,
                    ratingState = ratingState,
                    ticketState = ticketState,
                    onProductRatingChanged = onProductRatingChanged,
                    onSubmitRatings = onSubmitRatings,
                    onResetRatingState = onResetRatingState,
                    onSubmitTicket = onSubmitTicket,
                    onBackToHome = onBackToHome
                )
            }
        }
    }
}

@Composable
private fun OrderCompleteContent(
    order: Order,
    productRatings: Map<String, Int>,
    ratingState: OrderCompleteViewModel.RatingState,
    ticketState: OrderCompleteViewModel.TicketState,
    onProductRatingChanged: (String, Int) -> Unit,
    onSubmitRatings: () -> Unit,
    onResetRatingState: () -> Unit,
    onSubmitTicket: (String, String) -> Unit,
    onBackToHome: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    val imeInsets = WindowInsets.ime
    val density = LocalDensity.current
    val isKeyboardVisible by remember {
        derivedStateOf { imeInsets.getBottom(density) > 0 }
    }
    LaunchedEffect(Unit) { visible = true }

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 100.dp)
        ) {
            SuccessHeader()

            Spacer(modifier = Modifier.height(20.dp))

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 3 })
            ) {
                OrderIdCard(order = order)
            }

            Spacer(modifier = Modifier.height(12.dp))

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 3 })
            ) {
                StoreInfoCard(order = order)
            }

            Spacer(modifier = Modifier.height(12.dp))

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 3 })
            ) {
                ReceiptCard(order = order)
            }

            Spacer(modifier = Modifier.height(12.dp))

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 3 })
            ) {
                RateOrderCard(
                    orderItems = order.items,
                    productRatings = productRatings,
                    ratingState = ratingState,
                    onProductRatingChanged = onProductRatingChanged,
                    onSubmitRatings = onSubmitRatings,
                    onResetRatingState = onResetRatingState
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 3 })
            ) {
                SupportCard(
                    ticketState = ticketState,
                    onSubmitTicket = onSubmitTicket
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }


        if(!isKeyboardVisible){
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, StarbucksWhite, StarbucksWhite)
                        )
                    )
                    .imePadding()
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
}


@Composable
private fun SuccessHeader() {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "successIconScale"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(StarbucksGreen, StarbucksGreen.copy(alpha = 0.85f))
                )
            )
            .statusBarsPadding()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .size(72.dp)
                .graphicsLayer { scaleX = scale; scaleY = scale }
        )
        Text(
            text = "Order Complete!",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "Enjoy your coffee",
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.85f)
        )
    }
}


@Composable
private fun OrderIdCard(order: Order) {
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
                        color = StarbucksGreen,
                        letterSpacing = 0.5.sp
                    )
                }

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
                    text = "Completed at  ",
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


@Composable
private fun StoreInfoCard(order: Order) {
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
                    text = "Picked up from",
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

@Composable
private fun ReceiptCard(order: Order) {
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
                text = "Receipt",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = StarbucksBlack
            )

            Spacer(modifier = Modifier.height(12.dp))

            order.items.forEachIndexed { index, item ->
                ReceiptItemRow(item = item)
                if (index < order.items.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = StarbucksSurface,
                        thickness = 1.dp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = StarbucksSurface, thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            PriceRow(label = "Items Total", value = "$" + "%.2f".format(order.itemsTotal))
            Spacer(modifier = Modifier.height(6.dp))
            PriceRow(label = "Service Fee", value = "$" + "%.2f".format(order.serviceFee))
            Spacer(modifier = Modifier.height(6.dp))
            PriceRow(label = "Taxes (5%)", value = "$" + "%.2f".format(order.tax))

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = StarbucksSurface, thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))

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
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = StarbucksGreen
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(StarbucksMint.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Paid via Cash on Pickup",
                    fontSize = 12.sp,
                    color = StarbucksGreen,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun ReceiptItemRow(item: OrderItem) {
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
            Spacer(modifier = Modifier.height(3.dp))
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
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = StarbucksBlack
        )
    }
}

@Composable
private fun PriceRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 13.sp, color = StarbucksGray)
        Text(text = value, fontSize = 13.sp, color = StarbucksBlack, fontWeight = FontWeight.Medium)
    }
}


@Composable
private fun RateOrderCard(
    orderItems: List<OrderItem>,
    productRatings: Map<String, Int>,
    ratingState: OrderCompleteViewModel.RatingState,
    onProductRatingChanged: (String, Int) -> Unit,
    onSubmitRatings: () -> Unit,
    onResetRatingState: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (ratingState !is OrderCompleteViewModel.RatingState.Submitted)
                            Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { expanded = !expanded }
                            )
                        else Modifier
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "Rate Your Order",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = StarbucksBlack
                    )
                    Text(
                        text = "Help others by rating what you ordered",
                        fontSize = 11.sp,
                        color = StarbucksGray
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                if (ratingState !is OrderCompleteViewModel.RatingState.Submitted) {
                    val rotationAngle by animateFloatAsState(
                        targetValue = if (expanded) 180f else 0f,
                        animationSpec = tween(300),
                        label = "chevronRotation"
                    )
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(StarbucksMint, CircleShape)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { expanded = !expanded }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = if (expanded) "Collapse" else "Expand",
                            tint = StarbucksGreen,
                            modifier = Modifier
                                .size(20.dp)
                                .graphicsLayer { rotationZ = rotationAngle }
                        )
                    }
                }
            }


            if (ratingState is OrderCompleteViewModel.RatingState.Submitted) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(StarbucksMint.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = null,
                        tint = StarbucksGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            text = "Thank you for rating!",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = StarbucksGreen
                        )
                        Text(
                            text = "Your feedback helps improve our menu.",
                            fontSize = 11.sp,
                            color = StarbucksGray
                        )
                    }
                }
            }


            if (ratingState is OrderCompleteViewModel.RatingState.Error) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFEDEC), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "⚠️ ${ratingState.message}",
                        fontSize = 12.sp,
                        color = Color(0xFFE0483E),
                        lineHeight = 17.sp
                    )
                }
                TextButton(
                    onClick = onResetRatingState,
                    colors = ButtonDefaults.textButtonColors(contentColor = StarbucksGreen)
                ) {
                    Text("Dismiss", fontSize = 12.sp)
                }
            }


            if (expanded && ratingState !is OrderCompleteViewModel.RatingState.Submitted) {

                HorizontalDivider(color = StarbucksSurface, thickness = 1.dp)


                val uniqueItems = orderItems.distinctBy { it.productId }

                uniqueItems.forEachIndexed { index, item ->
                    ProductRatingRow(
                        item = item,
                        selectedStars = productRatings[item.productId] ?: 0,
                        onStarTap = { stars -> onProductRatingChanged(item.productId, stars) }
                    )
                    if (index < uniqueItems.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            color = StarbucksSurface,
                            thickness = 1.dp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                val hasAnyRating = productRatings.values.any { it > 0 }
                val isSubmitting = ratingState is OrderCompleteViewModel.RatingState.Submitting

                Button(
                    onClick = onSubmitRatings,
                    enabled = hasAnyRating && !isSubmitting,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StarbucksGreen)
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Submit Ratings",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (!hasAnyRating) {
                    Text(
                        text = "Tap the stars above to rate your items first",
                        fontSize = 11.sp,
                        color = StarbucksGray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}


@Composable
private fun ProductRatingRow(
    item: OrderItem,
    selectedStars: Int,
    onStarTap: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.productName,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = StarbucksBlack,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.width(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            for (i in 1..5) {
                val scale by animateFloatAsState(
                    targetValue = if (selectedStars >= i) 1.15f else 1f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "star_${item.productId}_$i"
                )
                Icon(
                    imageVector = if (selectedStars >= i) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = "$i star",
                    tint = if (selectedStars >= i) StarbucksGold else StarbucksGray.copy(alpha = 0.5f),
                    modifier = Modifier
                        .size(28.dp)
                        .graphicsLayer { scaleX = scale; scaleY = scale }
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onStarTap(i) }
                        )
                )
            }
        }
    }
}



private val issueOptions = listOf(
    "Wrong item received",
    "Missing item",
    "Quality issue",
    "Long wait time",
    "Rude staff",
    "Other"
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SupportCard(
    ticketState: OrderCompleteViewModel.TicketState,
    onSubmitTicket: (String, String) -> Unit
) {
    var selectedIssue by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(StarbucksMint, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SupportAgent,
                        contentDescription = null,
                        tint = StarbucksGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text(
                        text = "Need Help?",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = StarbucksBlack
                    )
                    Text(
                        text = "Raise a complaint or report an issue",
                        fontSize = 11.sp,
                        color = StarbucksGray
                    )
                }
            }

            if (!expanded && ticketState !is OrderCompleteViewModel.TicketState.Submitted) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(StarbucksSurface, RoundedCornerShape(12.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { expanded = true }
                        )
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Something wrong with your order?",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = StarbucksBlack
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = StarbucksGray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            if (ticketState is OrderCompleteViewModel.TicketState.Submitted) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(StarbucksMint.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Your complaint has been submitted. We'll look into it shortly.",
                        fontSize = 13.sp,
                        color = StarbucksGreen,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 18.sp
                    )
                }
            }

            if (expanded && ticketState !is OrderCompleteViewModel.TicketState.Submitted) {
                Text(
                    text = "What went wrong?",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = StarbucksBlack
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    issueOptions.forEach { issue ->
                        val isSelected = selectedIssue == issue
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50.dp))
                                .background(
                                    if (isSelected) StarbucksGreen else Color.Transparent
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) StarbucksGreen
                                    else StarbucksGreen.copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(50.dp)
                                )
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = { selectedIssue = issue }
                                )
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = issue,
                                fontSize = 12.sp,
                                color = if (isSelected) Color.White else StarbucksGreen,
                                fontWeight = if (isSelected) FontWeight.SemiBold
                                else FontWeight.Normal
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            "Describe your issue (optional)",
                            fontSize = 13.sp,
                            color = StarbucksGray
                        )
                    },
                    minLines = 3,
                    maxLines = 5,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = StarbucksGreen,
                        unfocusedBorderColor = StarbucksGray.copy(alpha = 0.4f),
                        cursorColor = StarbucksGreen
                    )
                )

                Button(
                    onClick = {
                        if (selectedIssue.isNotBlank()) {
                            onSubmitTicket(selectedIssue, description.trim())
                        }
                    },
                    enabled = selectedIssue.isNotBlank() &&
                            ticketState !is OrderCompleteViewModel.TicketState.Submitting,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StarbucksGreen)
                ) {
                    if (ticketState is OrderCompleteViewModel.TicketState.Submitting) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Submit Complaint",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun OrderCompleteErrorState(message: String, onBackToHome: () -> Unit) {
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