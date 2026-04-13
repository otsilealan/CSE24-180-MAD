package com.accommodation.ui.listings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.accommodation.data.database.entities.Listing
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListingsScreen(
    viewModel: ListingsViewModel,
    onListingClick: (Int) -> Unit,
    onFilterClick: () -> Unit
) {
    val listings by viewModel.listings.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Accommodation Finder") },
                actions = {
                    IconButton(onClick = onFilterClick) { Icon(Icons.Default.FilterList, "Filter") }
                }
            )
        }
    ) { padding ->
        if (listings.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No listings found", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 0.dp,
                    top = padding.calculateTopPadding(),
                    end = 0.dp,
                    bottom = padding.calculateBottomPadding() + 16.dp
                )
            ) {
                items(listings, key = { it.id }) { listing ->
                    ListingCard(
                        listing = listing,
                        distance = viewModel.distanceFromCampusKm(listing),
                        onClick = { onListingClick(listing.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ListingCard(listing: Listing, distance: Double, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.height(120.dp)) {
            AsyncImage(
                model = if (listing.imagePath.startsWith("/")) File(listing.imagePath) else listing.imagePath,
                contentDescription = listing.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(120.dp)
                    .fillMaxHeight()
            )
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    listing.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1
                )
                Text(
                    "BWP ${listing.price}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    listing.location,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (distance >= 0) {
                        Text(
                            "%.1f km from campus".format(distance),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    StatusBadge(listing.status)
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val containerColor = if (status == "Available") 
        MaterialTheme.colorScheme.primaryContainer 
    else 
        MaterialTheme.colorScheme.errorContainer
    val contentColor = if (status == "Available") 
        MaterialTheme.colorScheme.onPrimaryContainer 
    else 
        MaterialTheme.colorScheme.onErrorContainer
        
    Surface(
        color = containerColor,
        contentColor = contentColor,
        shape = MaterialTheme.shapes.extraSmall
    ) {
        Text(
            status,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall
        )
    }
}
