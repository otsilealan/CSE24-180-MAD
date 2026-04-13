package com.accommodation.ui.reservation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.accommodation.data.database.entities.Listing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    listing: Listing,
    studentId: Int,
    viewModel: ReservationViewModel,
    onSuccess: (Reservation: com.accommodation.data.database.entities.Reservation) -> Unit,
    onBack: () -> Unit
) {
    var cardNumber by remember { mutableStateOf("") }
    var expiry by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var showConfirm by remember { mutableStateOf(false) }
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state) {
        if (state is ReservationState.Success) {
            onSuccess((state as ReservationState.Success).reservation)
            viewModel.reset()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Secure Payment") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Payment Summary", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Divider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(listing.title, style = MaterialTheme.typography.bodyLarge)
                        Text("BWP ${listing.deposit}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                    }
                    Text(listing.location, style = MaterialTheme.typography.bodySmall)
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Card Information", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                
                OutlinedTextField(
                    value = cardNumber,
                    onValueChange = { if (it.length <= 16 && it.all { c -> c.isDigit() }) cardNumber = it },
                    label = { Text("Card Number") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.CreditCard, null) },
                    placeholder = { Text("0000 0000 0000 0000") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = expiry,
                        onValueChange = { if (it.length <= 5) expiry = it },
                        label = { Text("Expiry (MM/YY)") },
                        modifier = Modifier.weight(1f),
                        leadingIcon = { Icon(Icons.Default.DateRange, null) },
                        placeholder = { Text("MM/YY") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = cvv,
                        onValueChange = { if (it.length <= 3 && it.all { c -> c.isDigit() }) cvv = it },
                        label = { Text("CVV") },
                        modifier = Modifier.weight(1f),
                        leadingIcon = { Icon(Icons.Default.Lock, null) },
                        placeholder = { Text("123") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
            }

            if (state is ReservationState.Error) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        (state as ReservationState.Error).message,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Button(
                onClick = { showConfirm = true },
                enabled = cardNumber.length == 16 && expiry.length == 5 && cvv.length == 3 && state !is ReservationState.Loading,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                contentPadding = PaddingValues(16.dp)
            ) {
                if (state is ReservationState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Pay BWP ${listing.deposit}")
                }
            }
            
            Text(
                "Your payment information is encrypted and secure.",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (showConfirm) {
                AlertDialog(
                    onDismissRequest = { showConfirm = false },
                    title = { Text("Confirm Reservation") },
                    text = { Text("Are you sure you want to reserve ${listing.title} for BWP ${listing.deposit}?") },
                    confirmButton = { 
                        Button(onClick = { showConfirm = false; viewModel.reserve(listing, studentId) }) { 
                            Text("Confirm & Pay") 
                        } 
                    },
                    dismissButton = { 
                        TextButton(onClick = { showConfirm = false }) { 
                            Text("Cancel") 
                        } 
                    }
                )
            }
        }
    }
}
