package com.example.coffeeapp.ui.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
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
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.runtime.remember
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.coffeeapp.model.Category
import com.example.coffeeapp.model.Product
import com.example.coffeeapp.ui.components.HomeLoadingSkeleton
import com.example.coffeeapp.ui.theme.CoffeeBrown
import com.example.coffeeapp.ui.theme.StarbucksBlack
import com.example.coffeeapp.ui.theme.StarbucksGray
import com.example.coffeeapp.ui.theme.StarbucksGreen
import com.example.coffeeapp.ui.theme.StarbucksMint
import com.example.coffeeapp.ui.theme.StarbucksWhite
import kotlinx.coroutines.delay
import java.util.Calendar
import androidx.core.graphics.toColorInt
import kotlin.time.Duration.Companion.milliseconds


@Composable
fun HomeScreen(
    uiState: HomeViewModel.HomeUiState,
    onProductClick: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onRetry: () -> Unit
) {
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
                    onProductClick = onProductClick,
                    onCategorySelected = onCategorySelected
                )
            }
        }
    }
}


@Composable
private fun HomeContent(
    state: HomeViewModel.HomeUiState.Success,
    onProductClick: (String) -> Unit,
    onCategorySelected: (String) -> Unit
) {
    val productRows = state.products.chunked(2)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {

        item(key = "header") {
            HomeHeader(firstName = state.firstName)
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
                            onClick = { onProductClick(product.id) }
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
    onClick: () -> Unit
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
            .height(200.dp)
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        AsyncImage(
            model = product.imageUrl,
            contentDescription = product.name,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(130.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-12).dp)
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.4f))
                    )
                )
                .padding(horizontal = 10.dp, vertical = 10.dp)
        ) {
            Text(
                text = product.name,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
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

                AddButton(onClick = {
                /* Phase 7: cart logic */

                })
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
    val isPressed = remember { androidx.compose.runtime.mutableStateOf(false) }
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


@Preview(showBackground = true, backgroundColor = 0xFFF7F8F7)
@Composable
fun HomeScreenPreview() {
    val fakeProducts = listOf(
        Product(
            id = "1", name = "Caramel Latte", description = "Rich espresso with caramel",
            category = "Latte", imageUrl = "", cardColor = "#2C1810",
            isAvailable = true, isFeatured = true, rating = 4.8, totalRatings = 234,
            sizes = mapOf("small" to 250.0, "medium" to 320.0, "large" to 390.0)
        ),
        Product(
            id = "2", name = "Cold Brew", description = "Smooth cold brew coffee",
            category = "Cold Brew", imageUrl = "", cardColor = "#1A3A4A",
            isAvailable = true, isFeatured = true, rating = 4.6, totalRatings = 189,
            sizes = mapOf("regular" to 280.0, "large" to 350.0)
        ),
        Product(
            id = "3", name = "Matcha Latte", description = "Ceremonial grade matcha",
            category = "Matcha", imageUrl = "", cardColor = "#2D5016",
            isAvailable = true, isFeatured = false, rating = 4.7, totalRatings = 156,
            sizes = mapOf("small" to 270.0, "medium" to 340.0)
        ),
        Product(
            id = "4", name = "Cappuccino", description = "Classic Italian cappuccino",
            category = "Cappuccino", imageUrl = "", cardColor = "#4A2C17",
            isAvailable = true, isFeatured = false, rating = 4.5, totalRatings = 312,
            sizes = mapOf("small" to 220.0, "medium" to 290.0)
        )
    )

    val fakeCategories = listOf(
        Category(id = "0", name = "All", iconName = "", order = 0),
        Category(id = "1", name = "Espresso", iconName = "", order = 1),
        Category(id = "2", name = "Latte", iconName = "", order = 2),
        Category(id = "3", name = "Cold Brew", iconName = "", order = 3),
        Category(id = "4", name = "Matcha", iconName = "", order = 4),
    )

    HomeScreen(
        uiState = HomeViewModel.HomeUiState.Success(
            firstName = "Anup",
            products = fakeProducts,
            featuredProducts = fakeProducts.filter { it.isFeatured },
            categories = fakeCategories,
            selectedCategory = "All"
        ),
        onProductClick = {},
        onCategorySelected = {},
        onRetry = {}
    )
}

@Preview(showBackground = true)
@Composable
fun HomeScreenLoadingPreview() {
    HomeScreen(
        uiState = HomeViewModel.HomeUiState.Loading,
        onProductClick = {},
        onCategorySelected = {},
        onRetry = {}
    )
}