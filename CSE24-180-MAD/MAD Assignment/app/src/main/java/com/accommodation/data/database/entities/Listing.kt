package com.accommodation.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "listings")
data class Listing(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val providerId: Int,
    val title: String,
    val price: Double,          // BWP
    val location: String,       // Gaborone area
    val type: String,           // Single Room | Double Room | Bachelor Flat | 1-Bedroom | 2-Bedroom | Shared House
    val amenities: String,      // comma-separated: WiFi, Water, Electricity, Parking, Furnished, Security, Laundry
    val availabilityDate: Long, // epoch millis
    val deposit: Double,        // BWP
    val imagePath: String,
    val status: String = "Available" // "Available" or "Reserved"
)
