package com.accommodation.data.repository

import com.accommodation.data.database.dao.ListingDao
import com.accommodation.data.database.entities.Listing
import kotlinx.coroutines.flow.Flow

class ListingRepository(private val dao: ListingDao) {
    fun getAll(): Flow<List<Listing>> = dao.getAll()
    fun getByProvider(providerId: Int): Flow<List<Listing>> = dao.getByProvider(providerId)
    fun filter(minPrice: Double, maxPrice: Double, location: String, date: Long): Flow<List<Listing>> =
        dao.filter(minPrice, maxPrice, location, date)
    suspend fun findById(id: Int): Listing? = dao.findById(id)
    suspend fun insert(listing: Listing): Long = dao.insert(listing)
    suspend fun update(listing: Listing) = dao.update(listing)
}
