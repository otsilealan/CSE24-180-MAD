package com.accommodation.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val email: String,
    val passwordHash: String,
    val studentId: String?,   // null for Provider role
    val phone: String,
    val role: String          // "Student" or "Provider"
)
