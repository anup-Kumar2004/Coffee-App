package com.example.coffeeapp.ui.productdetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.coffeeapp.model.Product
import com.example.coffeeapp.ui.theme.StarbucksBlack
import com.example.coffeeapp.ui.theme.StarbucksGray
import com.example.coffeeapp.ui.theme.StarbucksGold
import com.example.coffeeapp.ui.theme.StarbucksGreen
import com.example.coffeeapp.ui.theme.StarbucksMint
import com.example.coffeeapp.ui.theme.StarbucksWhite
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job


private val FavoriteRed = Color(0xFFE0483E)

@Composable
fun ProductDetailScreen(
    uiState: ProductDetailViewModel.ProductDetailUiState,
    onSizeSelected: (String) -> Unit,
    onIncrementQuantity: () -> Unit,
    onDecrementQuantity: () -> Unit,
    onToggleFavorite: () -> Unit,
    onAddToCart: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(StarbucksWhite)) {
        when (uiState) {
            is ProductDetailViewModel.ProductDetailUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = StarbucksGreen)
                }
            }

            is ProductDetailViewModel.ProductDetailUiState.Error -> {
                ProductDetailErrorState(message = uiState.message, onNavigateBack = onNavigateBack)
            }

            is ProductDetailViewModel.ProductDetailUiState.Success -> {
                ProductDetailContent(
                    state = uiState,
                    onSizeSelected = onSizeSelected,
                    onIncrementQuantity = onIncrementQuantity,
                    onDecrementQuantity = onDecrementQuantity,
                    onToggleFavorite = onToggleFavorite,
                    onAddToCart = onAddToCart,
                    onNavigateBack = onNavigateBack
                )
            }
        }
    }
}

@Composable
private fun ProductDetailContent(
    state: ProductDetailViewModel.ProductDetailUiState.Success,
    onSizeSelected: (String) -> Unit,
    onIncrementQuantity: () -> Unit,
    onDecrementQuantity: () -> Unit,
    onToggleFavorite: () -> Unit,
    onAddToCart: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val product = state.product

    var cardVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { cardVisible = true }

    Box(modifier = Modifier.fillMaxSize()) {

        // Full-bleed hero image — fills the entire top area, no flat color
        // background. A bottom scrim fades it into the white card so there's
        // no harsh seam between photo and content.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colorStops = arrayOf(
                                0f to Color.Transparent,
                                0.7f to Color.Transparent,
                                1f to StarbucksWhite
                            )
                        )
                    )
            )
        }

        AnimatedVisibility(
            visible = cardVisible,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(450, easing = FastOutSlowInEasing)
            ) + fadeIn(tween(450)),
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 265.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .background(StarbucksWhite)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 28.dp)
            ) {
                ProductInfoSection(product = product)

                if (product.sizes.size > 1) {
                    Spacer(modifier = Modifier.height(20.dp))
                    SizeSelector(
                        sizes = product.sizes,
                        selectedSize = state.selectedSize,
                        onSizeSelected = onSizeSelected
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                AnimatedPrice(unitPrice = product.sizes[state.selectedSize] ?: 0.0)

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = product.description,
                    color = StarbucksGray,
                    fontSize = 14.sp,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                QuantityStepper(
                    quantity = state.quantity,
                    onIncrement = onIncrementQuantity,
                    onDecrement = onDecrementQuantity
                )

                Spacer(modifier = Modifier.height(96.dp))
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .size(40.dp)
                    .background(color = StarbucksMint, shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = StarbucksGreen
                )
            }

            FavoriteButton(isFavorited = state.isFavorited, onClick = onToggleFavorite)
        }


        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        val snackbarJob = remember { mutableStateOf<Job?>(null) }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 96.dp)
        ) { data ->
            Snackbar(
                snackbarData = data,
                containerColor = StarbucksGreen,
                contentColor = Color.White,
                shape = RoundedCornerShape(12.dp)
            )
        }

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
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            val totalPrice = (product.sizes[state.selectedSize] ?: 0.0) * state.quantity
            Button(
                onClick = {
                    onAddToCart()
                    snackbarJob.value?.cancel()
                    snackbarJob.value = scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Added to cart!",
                            duration = androidx.compose.material3.SnackbarDuration.Short
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StarbucksGreen)
            ) {
                Text(
                    text = "Add to Cart · $" + "%.2f".format(totalPrice),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }


    }
}

@Composable
private fun ProductInfoSection(product: Product) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(StarbucksMint)
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(text = product.category, color = StarbucksGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }

    Spacer(modifier = Modifier.height(12.dp))

    Text(text = product.name, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = StarbucksBlack)

    if (product.rating > 0) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = StarbucksGold,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "%.1f".format(product.rating),
                color = StarbucksBlack,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "(${product.totalRatings} ratings)",
                color = StarbucksGray,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun SizeSelector(
    sizes: Map<String, Double>,
    selectedSize: String,
    onSizeSelected: (String) -> Unit
) {
    val sizeKeys = remember(sizes) { sortedSizeKeys(sizes) }

    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        sizeKeys.forEach { size ->
            val isSelected = size == selectedSize

            val bgColor by animateColorAsState(
                targetValue = if (isSelected) StarbucksGreen else Color.Transparent,
                animationSpec = tween(250),
                label = "sizePillBg_$size"
            )
            val textColor by animateColorAsState(
                targetValue = if (isSelected) Color.White else StarbucksGreen,
                animationSpec = tween(250),
                label = "sizePillText_$size"
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .background(bgColor)
                    .then(
                        if (!isSelected) Modifier.border(
                            width = 1.dp,
                            color = StarbucksGreen.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(50.dp)
                        ) else Modifier
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onSizeSelected(size) }
                    )
                    .padding(horizontal = 18.dp, vertical = 10.dp)
            ) {
                Text(
                    text = size.replaceFirstChar { it.uppercase() },
                    color = textColor,
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
private fun AnimatedPrice(unitPrice: Double) {
    val scale = remember { Animatable(1f) }
    LaunchedEffect(unitPrice) {
        scale.snapTo(1.15f)
        scale.animateTo(1f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))
    }

    Text(
        text = "$" + "%.2f".format(unitPrice),
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold,
        color = StarbucksGreen,
        modifier = Modifier.graphicsLayer { scaleX = scale.value; scaleY = scale.value }
    )
}


@Composable
private fun QuantityStepper(
    quantity: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Quantity", color = StarbucksBlack, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .background(StarbucksMint)
                .border(width = 1.dp, color = StarbucksGreen.copy(alpha = 0.25f), shape = RoundedCornerShape(50.dp))
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            QuantityButton(
                icon = Icons.Default.Remove,
                contentDescription = "Decrease quantity",
                enabled = quantity > 1,
                onClick = onDecrement
            )
            Text(
                text = quantity.toString(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = StarbucksGreen,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(36.dp)
            )
            QuantityButton(
                icon = Icons.Default.Add,
                contentDescription = "Increase quantity",
                enabled = quantity < 10,
                onClick = onIncrement
            )
        }
    }
}

@Composable
private fun QuantityButton(
    icon: ImageVector,
    contentDescription: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.85f else 1f,
        animationSpec = tween(100),
        label = "qtyButtonScale"
    )

    Box(
        modifier = Modifier
            .size(32.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(CircleShape)
            .background(if (enabled) StarbucksGreen else StarbucksGreen.copy(alpha = 0.35f))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun FavoriteButton(isFavorited: Boolean, onClick: () -> Unit) {
    val scale = remember { Animatable(1f) }
    LaunchedEffect(isFavorited) {
        scale.snapTo(1.3f)
        scale.animateTo(1f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))
    }

    Box(
        modifier = Modifier
            .size(40.dp)
            .background(StarbucksWhite, CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isFavorited) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = if (isFavorited) "Remove from favorites" else "Add to favorites",
            tint = if (isFavorited) FavoriteRed else StarbucksGray,
            modifier = Modifier
                .size(20.dp)
                .graphicsLayer { scaleX = scale.value; scaleY = scale.value }
        )
    }
}

@Composable
private fun ProductDetailErrorState(message: String, onNavigateBack: () -> Unit) {
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
            Text(text = "Something went wrong", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = StarbucksBlack)
            Text(text = message, fontSize = 14.sp, color = StarbucksGray, textAlign = TextAlign.Center)
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

@Composable
private fun InteractionSource.collectIsPressedAsState(): androidx.compose.runtime.State<Boolean> {
    val isPressed = remember { mutableStateOf(false) }
    LaunchedEffect(this) {
        val interactions = mutableSetOf<Interaction>()
        this@collectIsPressedAsState.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> interactions.add(interaction)
                is PressInteraction.Release -> interactions.remove(interaction.press)
                is PressInteraction.Cancel -> interactions.remove(interaction.press)
            }
            isPressed.value = interactions.isNotEmpty()
        }
    }
    return isPressed
}

@Preview(showBackground = true)
@Composable
fun ProductDetailScreenPreview() {
    val fakeProduct = Product(
        id = "1",
        name = "Caramel Latte",
        description = "Rich espresso balanced with steamed milk and buttery caramel sauce, topped with whipped cream and a caramel drizzle.",
        category = "Latte",
        imageUrl = "",
        cardColor = "#2C1810",
        isAvailable = true,
        isFeatured = true,
        rating = 4.8,
        totalRatings = 234,
        sizes = mapOf("small" to 250.0, "medium" to 320.0, "large" to 390.0)
    )

    ProductDetailScreen(
        uiState = ProductDetailViewModel.ProductDetailUiState.Success(
            product = fakeProduct,
            selectedSize = "medium",
            quantity = 2,
            isFavorited = false
        ),
        onSizeSelected = {},
        onIncrementQuantity = {},
        onDecrementQuantity = {},
        onToggleFavorite = {},
        onAddToCart = {},
        onNavigateBack = {}
    )
}