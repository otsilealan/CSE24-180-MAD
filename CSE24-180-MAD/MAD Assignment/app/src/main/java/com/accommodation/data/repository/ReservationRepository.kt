package com.accommodation.data.repository

import com.accommodation.data.database.dao.ListingDao
import com.accommodation.data.database.dao.ReservationDao
import com.accommodation.data.database.entities.Listing
import com.accommodation.data.database.entities.Reservation
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class ReservationRepository(
    private val reservationDao: ReservationDao,
    private val listingDao: ListingDao
) {
    fun getByStudent(studentId: Int): Flow<List<Reservation>> = reservationDao.getByStudent(studentId)

    suspend fun reserve(listing: Listing, studentId: Int): Result<Reservation> {
        val fresh = listingDao.findById(listing.id) ?: return Result.failure(Exception("Listing not found"))
        if (fresh.status == "Reserved") return Result.failure(Exception("This listing has already been reserved"))
        val reservation = Reservation(
            listingId = listing.id,
            studentId = studentId,
            referenceNumber = "RES-${UUID.randomUUID().toString().take(8).uppercase()}",
            amount = listing.deposit,
            reservationDate = System.currentTimeMillis()
        )
        reservationDao.insert(reservation)
        listingDao.update(fresh.copy(status = "Reserved"))
        return Result.success(reservation)
    }
}
