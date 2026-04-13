package com.accommodation.data.database.dao

import androidx.room.*
import com.accommodation.data.database.entities.Reservation
import kotlinx.coroutines.flow.Flow

@Dao
interface ReservationDao {
    @Insert suspend fun insert(reservation: Reservation): Long
    @Query("SELECT * FROM reservations WHERE studentId = :studentId") fun getByStudent(studentId: Int): Flow<List<Reservation>>
    @Query("SELECT * FROM reservations WHERE listingId = :listingId LIMIT 1") suspend fun findByListing(listingId: Int): Reservation?
}
