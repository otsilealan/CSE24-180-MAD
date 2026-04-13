package com.accommodation.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_preferences")
data class UserPreferences(
    @PrimaryKey val userId: Int,
    val minPrice: Double = 0.0,
    val maxPrice: Double = Double.MAX_VALUE,
    val location: String = "",       // comma-separated areas, empty = any
    val availabilityDate: Long = 0L  // 0 = any
)
