package com.accommodation.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reservations")
data class Reservation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val listingId: Int,
    val studentId: Int,
    val referenceNumber: String,
    val amount: Double,
    val reservationDate: Long,
    val status: String = "Confirmed"
)
