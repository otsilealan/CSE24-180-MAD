package com.accommodation.data.database.dao

import androidx.room.*
import com.accommodation.data.database.entities.Listing
import kotlinx.coroutines.flow.Flow

@Dao
interface ListingDao {
    @Insert suspend fun insert(listing: Listing): Long
    @Update suspend fun update(listing: Listing)
    @Query("SELECT * FROM listings ORDER BY id DESC") fun getAll(): Flow<List<Listing>>
    @Query("SELECT * FROM listings WHERE id = :id LIMIT 1") suspend fun findById(id: Int): Listing?
    @Query("SELECT * FROM listings WHERE providerId = :providerId") fun getByProvider(providerId: Int): Flow<List<Listing>>
    @Query("""
        SELECT * FROM listings
        WHERE (:minPrice = 0 OR price >= :minPrice)
          AND (:maxPrice = 0 OR price <= :maxPrice)
          AND (:location = '' OR location = :location)
          AND (:date = 0 OR availabilityDate >= :date)
        ORDER BY id DESC
    """)
    fun filter(minPrice: Double, maxPrice: Double, location: String, date: Long): Flow<List<Listing>>
    @Query("SELECT COUNT(*) FROM listings") suspend fun count(): Int
}
