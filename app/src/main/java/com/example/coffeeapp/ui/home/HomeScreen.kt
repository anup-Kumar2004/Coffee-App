package com.example.coffeeapp.ui.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.coffeeapp.model.Category
import com.example.coffeeapp.model.Order
import com.example.coffeeapp.model.Product
import com.example.coffeeapp.ui.components.HomeLoadingSkeleton
import com.example.coffeeapp.ui.theme.CoffeeBrown
import com.example.coffeeapp.ui.theme.StarbucksBlack
import com.example.coffeeapp.ui.theme.StarbucksDarkGreen
import com.example.coffeeapp.ui.theme.StarbucksGold
import com.example.coffeeapp.ui.theme.StarbucksGray
import com.example.coffeeapp.ui.theme.StarbucksGreen
import com.example.coffeeapp.ui.theme.StarbucksMint
import com.example.coffeeapp.ui.theme.StarbucksWhite
import kotlinx.coroutines.delay
import java.util.Calendar
import androidx.core.graphics.toColorInt
import kotlin.time.Duration.Companion.milliseconds
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


@Composable
fun HomeScreen(
    uiState: HomeViewModel.HomeUiState,
    activeOrder: Order?,
    dismissedOrderId: String?,
    onProductClick: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onAddToCart: (Product) -> Unit,
    onTrackOrder: (String) -> Unit,
    onDismissActiveOrder: (String) -> Unit,
    onRetry: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val snackbarJob = remember { mutableStateOf<Job?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(StarbucksWhite)
    ) {
        when (uiState) {
            is HomeViewModel.HomeUiState.Loading -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item { HomeLoadingSkeleton() }
                }
            }
            is HomeViewModel.HomeUiState.Error -> {
                HomeErrorState(message = uiState.message, onRetry = onRetry)
            }
            is HomeViewModel.HomeUiState.Success -> {
                HomeContent(
                    state = uiState,
                    activeOrder = activeOrder,
                    dismissedOrderId = dismissedOrderId,
                    onProductClick = onProductClick,
                    onCategorySelected = onCategorySelected,
                    onTrackOrder = onTrackOrder,
                    onDismissActiveOrder = onDismissActiveOrder,
                    onAddToCart = { product ->
                        onAddToCart(product)
                        snackbarJob.value?.cancel()
                        snackbarJob.value = scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "${product.name} added to cart!",
                                duration = androidx.compose.material3.SnackbarDuration.Short
                            )
                        }
                    }
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
        ) { data ->
            Snackbar(
                snackbarData = data,
                containerColor = StarbucksGreen,
                contentColor = Color.White,
                shape = RoundedCornerShape(12.dp)
            )
        }

    }
}


@Composable
private fun HomeContent(
    state: HomeViewModel.HomeUiState.Success,
    activeOrder: Order?,
    dismissedOrderId: String?,
    onProductClick: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onTrackOrder: (String) -> Unit,
    onDismissActiveOrder: (String) -> Unit,
    onAddToCart: (Product) -> Unit
){
    val productRows = state.products.chunked(2)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {

        item(key = "header") {
            HomeHeader(firstName = state.firstName)
        }

        if (activeOrder != null) {
            item(key = "active_order") {
                AnimatedVisibility(
                    visible = activeOrder.orderId != dismissedOrderId,
                    exit = fadeOut(tween(250)) + shrinkVertically(tween(250))
                ) {
                    Column {
                        ActiveOrderTicket(
                            order = activeOrder,
                            onTrack = { onTrackOrder(activeOrder.orderId) },
                            onDismiss = { onDismissActiveOrder(activeOrder.orderId) }
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }

        if (state.featuredProducts.isNotEmpty()) {
            item(key = "hero") {
                Spacer(modifier = Modifier.height(8.dp))
                HeroBanner(
                    featuredProducts = state.featuredProducts,
                    onProductClick = onProductClick
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        item(key = "categories") {
            Text(
                text = "Our Menu",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = StarbucksBlack,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            CategoryPills(
                categories = state.categories,
                selectedCategory = state.selectedCategory,
                onCategorySelected = onCategorySelected
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        itemsIndexed(
            items = productRows,
            key = { index, _ -> "row_$index" }
        ) { index, rowProducts ->
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(tween(300, delayMillis = index * 60)) +
                        slideInVertically(
                            tween(300, delayMillis = index * 60),
                            initialOffsetY = { it / 4 }
                        )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowProducts.forEach { product ->
                        ProductCard(
                            product = product,
                            modifier = Modifier.weight(1f),
                            onClick = { onProductClick(product.id) },
                            onAddToCart = { onAddToCart(product) }
                        )
                    }

                    if (rowProducts.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}


/**
 * A premium banner for the user's active pending order — a single deep
 * green card rendered as a vertical stack so the full order ID and full
 * store address always have room to breathe, with a dedicated full-width
 * "Track order" row at the bottom rather than a cramped side tab.
 */
@Composable
private fun ActiveOrderTicket(
    order: Order,
    onTrack: () -> Unit,
    onDismiss: () -> Unit
) {
    var remainingSeconds by remember(order.orderId) {
        mutableLongStateOf(computeRemainingSeconds(order))
    }

    LaunchedEffect(order.orderId) {
        while (remainingSeconds > 0) {
            delay(1000.milliseconds)
            remainingSeconds = computeRemainingSeconds(order)
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "liveDotPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val countdownText = "%d:%02d".format(minutes, seconds)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(StarbucksGreen, StarbucksDarkGreen)
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 18.dp, top = 16.dp, end = 12.dp, bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .graphicsLayer {
                                    scaleX = pulseScale
                                    scaleY = pulseScale
                                    alpha = pulseAlpha
                                }
                                .background(StarbucksMint.copy(alpha = 0.5f), CircleShape)
                        )
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .background(StarbucksMint, CircleShape)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Order in progress",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = StarbucksMint,
                        letterSpacing = 0.3.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = countdownText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = StarbucksGold
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.14f))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onDismiss
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss",
                            tint = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "#${order.orderId}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 0.4.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = order.storeName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White.copy(alpha = 0.9f)
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = order.storeAddress,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    lineHeight = 16.sp
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.White.copy(alpha = 0.14f))
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onTrack
                    )
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Track order",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

private fun computeRemainingSeconds(order: Order): Long {
    val placedAtMillis = order.timestamp.toDate().time
    val expiryMillis = placedAtMillis + 30L * 60 * 1000
    val remainingMillis = expiryMillis - System.currentTimeMillis()
    return (remainingMillis / 1000).coerceAtLeast(0)
}


@Composable
private fun HomeHeader(firstName: String) {
    val greeting = remember {
        when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in 0..11 -> "Good morning,"
            in 12..17 -> "Good afternoon,"
            else -> "Good evening,"
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = greeting,
                fontSize = 14.sp,
                color = StarbucksGray,
                fontWeight = FontWeight.Normal
            )
            Text(
                text = firstName.ifBlank { "Coffee Lover" },
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = StarbucksGreen
            )
        }

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(StarbucksGreen),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = firstName.take(1).uppercase().ifBlank { "C" },
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


@Composable
private fun HeroBanner(
    featuredProducts: List<Product>,
    onProductClick: (String) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { featuredProducts.size })

    LaunchedEffect(Unit) {
        while (true) {
            delay(3000.milliseconds)
            val next = (pagerState.currentPage + 1) % featuredProducts.size
            pagerState.animateScrollToPage(next)
        }
    }

    Column {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 20.dp),
            pageSpacing = 12.dp
        ) { page ->
            HeroCard(
                product = featuredProducts[page],
                onClick = { onProductClick(featuredProducts[page].id) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(featuredProducts.size) { index ->
                val isSelected = pagerState.currentPage == index
                val dotColor by animateColorAsState(
                    targetValue = if (isSelected) StarbucksGreen else StarbucksMint,
                    animationSpec = tween(300),
                    label = "dotColor_$index"
                )
                val dotWidth by androidx.compose.animation.core.animateDpAsState(
                    targetValue = if (isSelected) 20.dp else 8.dp,
                    animationSpec = tween(300),
                    label = "dotWidth_$index"
                )
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .height(8.dp)
                        .width(dotWidth)
                        .clip(CircleShape)
                        .background(dotColor)
                )
            }
        }
    }
}


@Composable
private fun HeroCard(product: Product, onClick: () -> Unit) {
    val bgColor = remember(product.cardColor) {
        try {
            Color(product.cardColor.toColorInt())
        } catch (_: Exception) {
            CoffeeBrown
        }
    }

    // Dark-to-transparent scrim so text is always readable
    val scrimBrush = Brush.horizontalGradient(
        colors = listOf(
            Color.Black.copy(alpha = 0.55f),
            Color.Transparent
        )
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bgColor)
        ) {

            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(160.dp)
                    .align(Alignment.CenterEnd)
                    .offset(x = 8.dp)
                    .graphicsLayer { rotationZ = -5f }
            )


            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(scrimBrush)
            )

            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(20.dp)
                    .fillMaxWidth(0.55f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = product.name,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                val minPrice = product.sizes.values.minOrNull()
                if (minPrice != null) {
                    Text(
                        text = "from $" + "%.2f".format(minPrice),
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 13.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(Color.White)
                        .clickable(onClick = onClick)
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Order Now",
                        color = StarbucksGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}


@Composable
private fun CategoryPills(
    categories: List<Category>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(items = categories, key = { it.id.ifBlank { it.name } }) { category ->
            val isSelected = category.name == selectedCategory

            val bgColor by animateColorAsState(
                targetValue = if (isSelected) StarbucksGreen else Color.Transparent,
                animationSpec = tween(250),
                label = "pillBg_${category.name}"
            )
            val textColor by animateColorAsState(
                targetValue = if (isSelected) Color.White else StarbucksGreen,
                animationSpec = tween(250),
                label = "pillText_${category.name}"
            )
            val borderColor by animateColorAsState(
                targetValue = if (isSelected) StarbucksGreen else StarbucksGreen.copy(alpha = 0.5f),
                animationSpec = tween(250),
                label = "pillBorder_${category.name}"
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .background(bgColor)
                    .then(
                        if (!isSelected) Modifier.border(
                            width = 1.dp,
                            color = borderColor,
                            shape = RoundedCornerShape(50.dp)
                        ) else Modifier
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onCategorySelected(category.name) }
                    )

                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = category.name,
                    color = textColor,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
private fun ProductCard(
    product: Product,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onAddToCart: () -> Unit
) {
    val bgColor = remember(product.cardColor) {
        try {
            Color(product.cardColor.toColorInt())
        } catch (_: Exception) {
            CoffeeBrown
        }
    }

    val minPrice = product.sizes.values.minOrNull()

    Box(
        modifier = modifier
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        Column {
            // Fixed height image section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Text info section — always below image, never overlapping
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(bgColor)
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = product.name,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        if (minPrice != null) {
                            Text(
                                text = "$" + "%.2f".format(minPrice),
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        if (product.rating > 0) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFFFD700),
                                    modifier = Modifier.size(11.dp)
                                )
                                Text(
                                    text = "%.1f".format(product.rating),
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    AddButton(onClick = onAddToCart)
                }
            }
        }
    }
}


@Composable
private fun AddButton(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isPressed) 0.85f else 1f,
        animationSpec = tween(100),
        label = "addButtonScale"
    )

    Box(
        modifier = Modifier
            .size(28.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(CircleShape)
            .background(Color.White)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add to cart",
            tint = StarbucksGreen,
            modifier = Modifier.size(18.dp)
        )
    }
}


@Composable
private fun androidx.compose.foundation.interaction.InteractionSource.collectIsPressedAsState(): androidx.compose.runtime.State<Boolean> {
    val isPressed = remember { mutableStateOf(false) }
    LaunchedEffect(this) {
        val interactions = mutableSetOf<androidx.compose.foundation.interaction.Interaction>()
        this@collectIsPressedAsState.interactions.collect { interaction ->
            when (interaction) {
                is androidx.compose.foundation.interaction.PressInteraction.Press -> interactions.add(interaction)
                is androidx.compose.foundation.interaction.PressInteraction.Release -> interactions.remove(interaction.press)
                is androidx.compose.foundation.interaction.PressInteraction.Cancel -> interactions.remove(interaction.press)
            }
            isPressed.value = interactions.isNotEmpty()
        }
    }
    return isPressed
}


@Composable
private fun HomeErrorState(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
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
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Button(
                onClick = onRetry,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StarbucksGreen)
            ) {
                Text(text = "Try Again", fontSize = 15.sp)
            }
        }
    }
}

