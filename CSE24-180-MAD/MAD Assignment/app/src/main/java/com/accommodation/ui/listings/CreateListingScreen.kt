package com.accommodation.ui.listings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.accommodation.data.database.entities.Listing
import com.accommodation.utils.ImageUtils
import java.text.SimpleDateFormat
import java.util.*

private val LOCATIONS = listOf("Gaborone West","Gaborone North","Broadhurst","Tlokweng","Mogoditshane","Phakalane","Block 8","Block 9","Extension 2","Bontleng")
private val TYPES = listOf("Single Room","Double Room","Bachelor Flat","1-Bedroom","2-Bedroom","Shared House")
private val AMENITIES = listOf("WiFi","Water","Electricity","Parking","Furnished","Security","Laundry")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateListingScreen(
    providerId: Int,
    viewModel: ListingsViewModel,
    onCreated: () -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var deposit by remember { mutableStateOf("") }
    var location by remember { mutableStateOf(LOCATIONS[0]) }
    var type by remember { mutableStateOf(TYPES[0]) }
    var selectedAmenities by remember { mutableStateOf(setOf<String>()) }
    var availabilityDate by remember { mutableStateOf(System.currentTimeMillis() + 7 * 86400000L) }
    var imagePath by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var locationExpanded by remember { mutableStateOf(false) }
    var typeExpanded by remember { mutableStateOf(false) }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { imagePath = ImageUtils.saveImage(context, it) ?: "" }
    }

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = availabilityDate)

    Scaffold(
        topBar = { TopAppBar(title = { Text("New Listing") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text("Listing Details", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

            OutlinedTextField(
                value = title, 
                onValueChange = { title = it }, 
                label = { Text("Property Title") }, 
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = price, 
                    onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) price = it }, 
                    label = { Text("Rent (BWP)") }, 
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
                OutlinedTextField(
                    value = deposit, 
                    onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) deposit = it }, 
                    label = { Text("Deposit (BWP)") }, 
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
            }

            ExposedDropdownMenuBox(expanded = locationExpanded, onExpandedChange = { locationExpanded = it }) {
                OutlinedTextField(
                    value = location, 
                    onValueChange = {}, 
                    readOnly = true, 
                    label = { Text("Location") }, 
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = locationExpanded) }
                )
                ExposedDropdownMenu(expanded = locationExpanded, onDismissRequest = { locationExpanded = false }) {
                    LOCATIONS.forEach { DropdownMenuItem(text = { Text(it) }, onClick = { location = it; locationExpanded = false }) }
                }
            }

            ExposedDropdownMenuBox(expanded = typeExpanded, onExpandedChange = { typeExpanded = it }) {
                OutlinedTextField(
                    value = type, 
                    onValueChange = {}, 
                    readOnly = true, 
                    label = { Text("Accommodation Type") }, 
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) }
                )
                ExposedDropdownMenu(expanded = typeExpanded, onDismissRequest = { typeExpanded = false }) {
                    TYPES.forEach { DropdownMenuItem(text = { Text(it) }, onClick = { type = it; typeExpanded = false }) }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Amenities", style = MaterialTheme.typography.labelLarge)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    AMENITIES.forEach { amenity ->
                        FilterChip(
                            selected = amenity in selectedAmenities,
                            onClick = { selectedAmenities = if (amenity in selectedAmenities) selectedAmenities - amenity else selectedAmenities + amenity },
                            label = { Text(amenity) },
                            leadingIcon = if (amenity in selectedAmenities) {
                                { Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp)) }
                            } else null
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Available From: ${SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(availabilityDate))}", style = MaterialTheme.typography.labelLarge)
                DatePicker(
                    state = datePickerState, 
                    showModeToggle = false,
                    title = null,
                    headline = null,
                    modifier = Modifier.fillMaxWidth()
                )
                LaunchedEffect(datePickerState.selectedDateMillis) {
                    datePickerState.selectedDateMillis?.let { availabilityDate = it }
                }
            }

            OutlinedButton(
                onClick = { imagePicker.launch("image/*") }, 
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp)
            ) {
                Icon(Icons.Default.AddAPhoto, null)
                Spacer(Modifier.width(8.dp))
                Text(if (imagePath.isNotBlank()) "Image Attached ✓" else "Upload Property Photo *")
            }

            if (error.isNotBlank()) {
                Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Button(
                onClick = {
                    when {
                        title.isBlank() -> error = "Title is required"
                        price.toDoubleOrNull() == null -> error = "Valid price required"
                        deposit.toDoubleOrNull() == null -> error = "Valid deposit required"
                        imagePath.isBlank() -> error = "Image is required"
                        else -> {
                            viewModel.insertListing(Listing(
                                providerId = providerId,
                                title = title,
                                price = price.toDouble(),
                                location = location,
                                type = type,
                                amenities = selectedAmenities.joinToString(","),
                                availabilityDate = availabilityDate,
                                deposit = deposit.toDouble(),
                                imagePath = imagePath
                            ))
                            onCreated()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                contentPadding = PaddingValues(16.dp)
            ) { Text("Post Listing") }
            
            Spacer(Modifier.height(24.dp))
        }
    }
}
