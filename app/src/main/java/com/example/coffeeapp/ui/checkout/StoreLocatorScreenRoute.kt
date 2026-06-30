package com.example.coffeeapp.ui.checkout

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.example.coffeeapp.navigation.Screen

@Composable
fun StoreLocatorScreenRoute(
    navController: NavController,
    viewModel: StoreLocatorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val orderPlacementState by viewModel.orderPlacementState.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.onPermissionResult(granted)
    }

    // If the user grants permission via Settings and comes back to this screen, re-check.
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.checkPermissionAndLoad()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // Navigate to Order Pickup once the order is successfully written to Firestore.
    LaunchedEffect(orderPlacementState) {
        val state = orderPlacementState
        if (state is StoreLocatorViewModel.OrderPlacementState.Success) {
            navController.navigate(Screen.OrderPickup.createRoute(state.orderId)) {
                popUpTo(Screen.MainScreen.route) { inclusive = true }
            }
        }
    }

    StoreLocatorScreen(
        uiState = uiState,
        onNavigateBack = { navController.navigateUp() },
        onRequestPermission = { permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) },
        onOpenSettings = {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
            context.startActivity(intent)
        },
        onRetry = { viewModel.checkPermissionAndLoad() },
        onSelectStore = { storeWithDistance ->
            viewModel.placeOrder(storeWithDistance.store)
        }
    )

    when (val placementState = orderPlacementState) {
        is StoreLocatorViewModel.OrderPlacementState.Placing -> {
            AlertDialog(
                onDismissRequest = { /* not dismissible while placing */ },
                confirmButton = {},
                title = { Text("Placing your order…") },
                text = { CircularProgressIndicator() }
            )
        }
        is StoreLocatorViewModel.OrderPlacementState.Error -> {
            AlertDialog(
                onDismissRequest = { viewModel.resetOrderPlacementState() },
                confirmButton = {
                    TextButton(onClick = { viewModel.resetOrderPlacementState() }) {
                        Text("OK")
                    }
                },
                title = { Text("Couldn't place order") },
                text = { Text(placementState.message) }
            )
        }
        else -> Unit
    }
}