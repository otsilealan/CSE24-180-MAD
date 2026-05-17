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

    // Realistic BWP monthly rents per type (min to max range)
    private val priceRanges = mapOf(
        "Single Room"    to (800..1400),
        "Double Room"    to (1000..1800),
        "Bachelor Flat"  to (1200..2200),
        "1-Bedroom"      to (1500..2800),
        "2-Bedroom"      to (2200..3500),
        "Shared House"   to (700..1200)
    )

    // Real accommodation photos from Unsplash (free, no auth needed)
    // One image per accommodation type for relevance
    private val typeImages = mapOf(
        "Single Room"   to "https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=600&q=80",
        "Double Room"   to "https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=600&q=80",
        "Bachelor Flat" to "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=600&q=80",
        "1-Bedroom"     to "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=600&q=80",
        "2-Bedroom"     to "https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?w=600&q=80",
        "Shared House"  to "https://images.unsplash.com/photo-1568605114967-8130f3a36994?w=600&q=80"
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
        val rng = java.util.Random(42) // fixed seed for reproducibility

        // 55 listings across 5 providers (ids 51–55)
        repeat(55) { i ->
            cal.timeInMillis = System.currentTimeMillis()
            cal.add(Calendar.DAY_OF_YEAR, 7 + (i % 30))
            val type = types[i % types.size]
            val range = priceRanges[type]!!
            val price = (range.first + rng.nextInt(range.last - range.first + 1)).toDouble()
            val deposit = (price * (if (i % 3 == 0) 1.0 else 0.5)).let {
                // round to nearest 50
                (Math.round(it / 50.0) * 50).toDouble()
            }
            db.listingDao().insert(Listing(
                providerId = 51 + (i % 5),
                title = "${type} in ${areas[i % areas.size]}",
                price = price,
                location = areas[i % areas.size],
                type = type,
                amenities = amenitySets[i % amenitySets.size],
                availabilityDate = cal.timeInMillis,
                deposit = deposit,
                imagePath = typeImages[type] ?: typeImages.values.first()
            ))
        }
    }
}
