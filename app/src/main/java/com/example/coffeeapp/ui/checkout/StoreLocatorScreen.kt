package com.example.coffeeapp.ui.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coffeeapp.ui.theme.StarbucksBlack
import com.example.coffeeapp.ui.theme.StarbucksGray
import com.example.coffeeapp.ui.theme.StarbucksGreen
import com.example.coffeeapp.ui.theme.StarbucksMint
import com.example.coffeeapp.ui.theme.StarbucksWhite
import androidx.compose.ui.draw.clip
import com.example.coffeeapp.ui.components.rememberShimmerBrush


@Composable
fun StoreLocatorScreen(
    uiState: StoreLocatorViewModel.StoreLocatorUiState,
    onNavigateBack: () -> Unit,
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit,
    onRetry: () -> Unit,
    onSelectStore: (StoreWithDistance) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(StarbucksWhite)
            .statusBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            StoreLocatorTopBar(onNavigateBack = onNavigateBack)

            when (uiState) {
                is StoreLocatorViewModel.StoreLocatorUiState.Loading -> {
                    StoreLocatorLoadingSkeleton()
                }
                is StoreLocatorViewModel.StoreLocatorUiState.PermissionRequired -> {
                    PermissionRequiredState(
                        onRequestPermission = onRequestPermission,
                        onOpenSettings = onOpenSettings
                    )
                }
                is StoreLocatorViewModel.StoreLocatorUiState.Error -> {
                    StoreLocatorErrorState(message = uiState.message, onRetry = onRetry)
                }
                is StoreLocatorViewModel.StoreLocatorUiState.Success -> {
                    if (uiState.stores.isEmpty()) {
                        NoStoresFoundState()
                    } else {
                        StoreList(stores = uiState.stores, onSelectStore = onSelectStore)
                    }
                }
            }
        }
    }
}

@Composable
private fun StoreLocatorTopBar(onNavigateBack: () -> Unit) {
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
                text = "Find a Store",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = StarbucksBlack
            )
            Text(
                text = "Pick the store you'll collect your order from",
                fontSize = 12.sp,
                color = StarbucksGray
            )
        }
    }
}

@Composable
private fun PermissionRequiredState(
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(40.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .background(StarbucksMint, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = StarbucksGreen,
                    modifier = Modifier.size(40.dp)
                )
            }
            Text(
                text = "Location Access Needed",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = StarbucksBlack,
                textAlign = TextAlign.Center
            )
            Text(
                text = "We use your location to find the nearest Crema stores for pickup. Your location is never stored or shared.",
                fontSize = 14.sp,
                color = StarbucksGray,
                textAlign = TextAlign.Center
            )
            Button(
                onClick = onRequestPermission,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StarbucksGreen)
            ) {
                Text(text = "Allow Location Access", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
            TextButton(onClick = onOpenSettings) {
                Text(text = "Open App Settings", color = StarbucksGreen, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun StoreLocatorErrorState(message: String, onRetry: () -> Unit) {
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
                onClick = onRetry,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StarbucksGreen)
            ) {
                Text(text = "Try Again", fontSize = 15.sp)
            }
        }
    }
}

@Composable
private fun NoStoresFoundState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.LocationOff,
                contentDescription = null,
                tint = StarbucksGray,
                modifier = Modifier.size(64.dp)
            )
            Text(
                text = "No Stores Found Nearby",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = StarbucksBlack
            )
            Text(
                text = "We couldn't find any Crema stores near your current location.",
                fontSize = 14.sp,
                color = StarbucksGray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun StoreList(
    stores: List<StoreWithDistance>,
    onSelectStore: (StoreWithDistance) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(stores, key = { it.store.id }) { storeWithDistance ->
            StoreCard(
                storeWithDistance = storeWithDistance,
                onSelect = { onSelectStore(storeWithDistance) }
            )
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun StoreCard(
    storeWithDistance: StoreWithDistance,
    onSelect: () -> Unit
) {
    val store = storeWithDistance.store
    val distanceText = remember(storeWithDistance.distanceKm) {
        if (storeWithDistance.distanceKm < 1.0) {
            "${(storeWithDistance.distanceKm * 1000).toInt()} m away"
        } else {
            "%.1f km away".format(storeWithDistance.distanceKm)
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = store.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = StarbucksBlack
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = store.address,
                        fontSize = 12.sp,
                        color = StarbucksGray,
                        lineHeight = 16.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .background(StarbucksMint, RoundedCornerShape(50.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = distanceText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = StarbucksGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onSelect,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StarbucksGreen)
            ) {
                Text(text = "Select this Store", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun StoreLocatorLoadingSkeleton() {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(5) {
            StoreCardSkeleton()
        }
    }
}

@Composable
private fun StoreCardSkeleton() {
    val brush = rememberShimmerBrush()
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Store name
                    Box(
                        modifier = Modifier
                            .width(160.dp)
                            .height(16.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(brush)
                    )
                    // Address line 1
                    Box(
                        modifier = Modifier
                            .width(200.dp)
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(brush)
                    )
                    // Address line 2
                    Box(
                        modifier = Modifier
                            .width(140.dp)
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(brush)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                // Distance pill
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(28.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(brush)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Select button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(brush)
            )
        }
    }
}
