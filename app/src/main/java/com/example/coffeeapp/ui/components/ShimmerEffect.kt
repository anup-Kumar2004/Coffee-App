package com.example.coffeeapp.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.example.coffeeapp.ui.theme.StarbucksMint
import com.example.coffeeapp.ui.theme.StarbucksSurface


@Composable
fun rememberShimmerBrush(): Brush {
    val shimmerColors = listOf(
        StarbucksSurface,
        StarbucksMint.copy(alpha = 0.6f),
        StarbucksSurface
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim - 300f, translateAnim - 300f),
        end = Offset(translateAnim, translateAnim)
    )
}


@Composable
fun ShimmerBox(
    modifier: Modifier,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(8.dp)
) {
    val brush = rememberShimmerBrush()
    Box(
        modifier = modifier
            .clip(shape)
            .background(brush)
    )
}


@Composable
fun HomeHeaderSkeleton() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ShimmerBox(
                modifier = Modifier
                    .width(140.dp)
                    .height(14.dp),
                shape = RoundedCornerShape(6.dp)
            )
            ShimmerBox(
                modifier = Modifier
                    .width(100.dp)
                    .height(22.dp),
                shape = RoundedCornerShape(6.dp)
            )
        }
        ShimmerBox(
            modifier = Modifier.size(48.dp),
            shape = CircleShape
        )
    }
}


@Composable
fun HeroBannerSkeleton() {
    ShimmerBox(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(200.dp),
        shape = RoundedCornerShape(24.dp)
    )
}


@Composable
fun CategoryPillsSkeleton() {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(5) {
            ShimmerBox(
                modifier = Modifier
                    .width(80.dp)
                    .height(36.dp),
                shape = RoundedCornerShape(50.dp)
            )
        }
    }
}


@Composable
fun ProductCardSkeleton(modifier: Modifier = Modifier) {
    val brush = rememberShimmerBrush()
    Box(
        modifier = modifier
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(brush)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            ShimmerBox(
                modifier = Modifier
                    .width(80.dp)
                    .height(14.dp),
                shape = RoundedCornerShape(4.dp)
            )
            ShimmerBox(
                modifier = Modifier
                    .width(50.dp)
                    .height(12.dp),
                shape = RoundedCornerShape(4.dp)
            )
        }
    }
}


@Composable
fun HomeLoadingSkeleton() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        HomeHeaderSkeleton()
        HeroBannerSkeleton()
        CategoryPillsSkeleton()

        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            repeat(2) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ProductCardSkeleton(modifier = Modifier.weight(1f))
                    ProductCardSkeleton(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}