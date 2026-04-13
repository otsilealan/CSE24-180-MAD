package com.accommodation.data

import com.accommodation.data.database.AppDatabase
import com.accommodation.data.database.entities.Listing
import com.accommodation.data.database.entities.User
import com.accommodation.utils.ValidationUtils
import java.util.Calendar

object SeedData {
    private val areas = listOf("Gaborone West", "Gaborone North", "Broadhurst", "Tlokweng", "Mogoditshane", "Phakalane", "Block 8", "Block 9", "Extension 2", "Bontleng")
    private val types = listOf("Single Room", "Double Room", "Bachelor Flat", "1-Bedroom", "2-Bedroom", "Shared House")
    private val amenitySets = listOf(
        "WiFi,Water,Electricity",
        "WiFi,Water,Electricity,Furnished",
        "Water,Electricity,Parking",
        "WiFi,Water,Electricity,Security,Laundry",
        "WiFi,Water,Electricity,Parking,Furnished,Security"
    )

    suspend fun seed(db: AppDatabase) {
        if (db.userDao().count() > 0) return // already seeded

        // 50 students
        repeat(50) { i ->
            db.userDao().insert(User(
                email = "student${i + 1}@ub.ac.bw",
                passwordHash = ValidationUtils.hashPassword("Pass@${i + 1}word"),
                studentId = "UB${200000 + i}",
                phone = "7${(1000000..9999999).random()}",
                role = "Student"
            ))
        }

        // 5 providers
        repeat(5) { i ->
            db.userDao().insert(User(
                email = "provider${i + 1}@mail.com",
                passwordHash = ValidationUtils.hashPassword("Prov@${i + 1}pass"),
                studentId = null,
                phone = "7${(1000000..9999999).random()}",
                role = "Provider"
            ))
        }

        val cal = Calendar.getInstance()

        // 55 listings across 5 providers (ids 51–55)
        repeat(55) { i ->
            cal.timeInMillis = System.currentTimeMillis()
            cal.add(Calendar.DAY_OF_YEAR, 7 + (i % 30))
            db.listingDao().insert(Listing(
                providerId = 51 + (i % 5),
                title = "${types[i % types.size]} in ${areas[i % areas.size]}",
                price = (1500 + (i * 97) % 3500).toDouble(),
                location = areas[i % areas.size],
                type = types[i % types.size],
                amenities = amenitySets[i % amenitySets.size],
                availabilityDate = cal.timeInMillis,
                deposit = (500 + (i * 43) % 1500).toDouble(),
                imagePath = "placeholder_${i % 5}"
            ))
        }
    }
}
