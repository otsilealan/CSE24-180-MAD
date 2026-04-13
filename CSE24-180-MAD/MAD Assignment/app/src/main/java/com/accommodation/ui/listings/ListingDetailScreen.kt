package com.accommodation.ui.listings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.accommodation.data.database.entities.Listing
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListingDetailScreen(
    listing: Listing,
    isStudent: Boolean,
    onBack: () -> Unit,
    onReserve: () -> Unit,
    onViewRoute: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Details") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } },
                actions = {
                    TextButton(onClick = onViewRoute) {
                        Icon(Icons.Default.Map, null)
                        Spacer(Modifier.width(4.dp))
                        Text("Route")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            AsyncImage(
                model = if (listing.imagePath.startsWith("/")) File(listing.imagePath) else listing.imagePath,
                contentDescription = listing.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )
            
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(listing.title, style = MaterialTheme.typography.headlineSmall)
                        Text(listing.location, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    StatusBadge(listing.status)
                }

                Divider()

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DetailRow("Rent", "BWP ${listing.price} / month")
                    DetailRow("Deposit", "BWP ${listing.deposit}")
                    DetailRow("Type", listing.type)
                    DetailRow("Available from", SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(listing.availabilityDate)))
                }

                Divider()

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Amenities", style = MaterialTheme.typography.titleMedium)
                    Text(
                        listing.amenities.split(",").joinToString(" • "),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (isStudent && listing.status == "Available") {
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = onReserve,
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        Text("Reserve with BWP ${listing.deposit} Deposit")
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}
