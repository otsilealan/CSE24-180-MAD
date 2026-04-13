package com.accommodation.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

private val CAMPUS = LatLng(-24.6553, 25.9086) // UB Main Campus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    listingTitle: String,
    listingLat: Double,
    listingLon: Double,
    onBack: () -> Unit
) {
    val listingPos = LatLng(listingLat, listingLon)
    val cameraState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(listingPos, 14f)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Route Map") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            GoogleMap(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                cameraPositionState = cameraState,
                uiSettings = MapUiSettings(zoomControlsEnabled = true)
            ) {
                Marker(
                    state = MarkerState(position = CAMPUS), 
                    title = "UB Main Campus",
                    snippet = "Starting Point"
                )
                Marker(
                    state = MarkerState(position = listingPos), 
                    title = listingTitle,
                    snippet = "Destination"
                )
                Polyline(
                    points = listOf(CAMPUS, listingPos), 
                    color = MaterialTheme.colorScheme.primary, 
                    width = 8f
                )
            }
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        Icons.Default.LocationOn, 
                        contentDescription = null, 
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Column {
                        Text("Destination", style = MaterialTheme.typography.labelMedium)
                        Text(listingTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("Route from UB Main Campus", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
