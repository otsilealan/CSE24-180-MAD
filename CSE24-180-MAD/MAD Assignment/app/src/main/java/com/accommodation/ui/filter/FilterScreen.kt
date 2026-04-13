package com.accommodation.ui.filter

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.accommodation.ui.listings.FilterParams

private val LOCATIONS = listOf("", "Gaborone West","Gaborone North","Broadhurst","Tlokweng","Mogoditshane","Phakalane","Block 8","Block 9","Extension 2","Bontleng")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterScreen(
    viewModel: FilterViewModel,
    userId: Int,
    onApply: (FilterParams) -> Unit,
    onDismiss: () -> Unit
) {
    val params by viewModel.params.collectAsState()
    var minPrice by remember { mutableStateOf(params.minPrice.toString().let { if (it == "0.0") "" else it }) }
    var maxPrice by remember { mutableStateOf(params.maxPrice.toString().let { if (it == "0.0") "" else it }) }
    var location by remember { mutableStateOf(params.location) }
    var date by remember { mutableStateOf(params.date) }
    var locationExpanded by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = if (date > 0) date else null)

    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let { date = it }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            "Filter Listings",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Price Range (BWP)", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = minPrice,
                    onValueChange = { minPrice = it },
                    label = { Text("Min") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = maxPrice,
                    onValueChange = { maxPrice = it },
                    label = { Text("Max") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }
        }

        ExposedDropdownMenuBox(
            expanded = locationExpanded,
            onExpandedChange = { locationExpanded = it }
        ) {
            OutlinedTextField(
                value = if (location.isBlank()) "Any area" else location,
                onValueChange = {},
                readOnly = true,
                label = { Text("Location") },
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = locationExpanded) }
            )
            ExposedDropdownMenu(
                expanded = locationExpanded,
                onDismissRequest = { locationExpanded = false }
            ) {
                LOCATIONS.forEach { area ->
                    DropdownMenuItem(
                        text = { Text(if (area.isBlank()) "Any area" else area) },
                        onClick = { location = area; locationExpanded = false }
                    )
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Available from", style = MaterialTheme.typography.labelLarge)
            DatePicker(
                state = datePickerState,
                showModeToggle = false,
                modifier = Modifier.fillMaxWidth(),
                title = null,
                headline = null
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = {
                    val fp = FilterParams(
                        minPrice = minPrice.toDoubleOrNull() ?: 0.0,
                        maxPrice = maxPrice.toDoubleOrNull() ?: 0.0,
                        location = location,
                        date = date
                    )
                    viewModel.update(fp)
                    onApply(fp)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Apply Filters")
            }

            OutlinedButton(
                onClick = { viewModel.savePreferences(userId) },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Save to My Preferences")
            }

            TextButton(
                onClick = { viewModel.clear(); onApply(FilterParams()) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Clear All", color = MaterialTheme.colorScheme.error)
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}
