package com.example.coffeeapp.ui.checkout

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.coffeeapp.navigation.Screen
import com.example.coffeeapp.ui.theme.StarbucksGray
import com.example.coffeeapp.ui.theme.StarbucksGreen
import com.example.coffeeapp.ui.theme.StarbucksWhite
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.coffeeapp.ui.theme.StarbucksBlack
import com.example.coffeeapp.ui.theme.StarbucksMint
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog

@Composable
fun OrderCompleteScreenRoute(
    navController: NavController,
    viewModel: OrderCompleteViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val productRatings by viewModel.productRatings.collectAsState()
    val ratingState by viewModel.ratingState.collectAsState()
    val ticketState by viewModel.ticketState.collectAsState()

    // ── Ticket submission dialogs ─────────────────────────────────────────────
    when (val state = ticketState) {
        is OrderCompleteViewModel.TicketState.Submitted -> {
            var showDialog by remember { mutableStateOf(true) }
            if (showDialog) {
                TicketRaisedDialog(
                    onDismiss = { showDialog = false }
                )
            }
        }
        is OrderCompleteViewModel.TicketState.Error -> {
            AlertDialog(
                onDismissRequest = { viewModel.resetTicketState() },
                confirmButton = {
                    TextButton(onClick = { viewModel.resetTicketState() }) {
                        Text("OK", color = StarbucksGreen)
                    }
                },
                title = { Text("Couldn't Submit") },
                text = { Text(state.message, color = StarbucksGray) },
                containerColor = StarbucksWhite
            )
        }
        else -> Unit
    }

    // ── Pure UI screen ────────────────────────────────────────────────────────
    OrderCompleteScreen(
        uiState = uiState,
        productRatings = productRatings,
        ratingState = ratingState,
        ticketState = ticketState,
        onProductRatingChanged = { productId, stars ->
            viewModel.setProductRating(productId, stars)
        },
        onSubmitRatings = { viewModel.submitProductRatings() },
        onResetRatingState = { viewModel.resetRatingState() },
        onSubmitTicket = { issueType, description ->
            viewModel.submitTicket(issueType, description)
        },
        onBackToHome = {
            navController.navigate(Screen.MainScreen.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    )

}

@Composable
private fun TicketRaisedDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = StarbucksWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(StarbucksMint, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = StarbucksGreen,
                        modifier = Modifier.size(36.dp)
                    )
                }
                Text(
                    text = "Ticket Raised!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = StarbucksBlack
                )
                Text(
                    text = "We've received your complaint and will look into it shortly. You can track this via your Order ID.",
                    fontSize = 14.sp,
                    color = StarbucksGray,
                    textAlign = TextAlign.Center
                )
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StarbucksGreen)
                ) {
                    Text(
                        text = "Done",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

