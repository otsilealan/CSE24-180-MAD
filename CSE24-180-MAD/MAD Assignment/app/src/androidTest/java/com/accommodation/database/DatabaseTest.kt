package com.accommodation.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.accommodation.data.database.AppDatabase
import com.accommodation.data.database.dao.ListingDao
import com.accommodation.data.database.dao.ReservationDao
import com.accommodation.data.database.dao.UserDao
import com.accommodation.data.database.entities.Listing
import com.accommodation.data.database.entities.Reservation
import com.accommodation.data.database.entities.User
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseTest {

    private lateinit var db: AppDatabase
    private lateinit var userDao: UserDao
    private lateinit var listingDao: ListingDao
    private lateinit var reservationDao: ReservationDao

    @Before fun setUp() {
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(ctx, AppDatabase::class.java).build()
        userDao = db.userDao()
        listingDao = db.listingDao()
        reservationDao = db.reservationDao()
    }

    @After fun tearDown() { db.close() }

    // --- User ---

    @Test fun insertAndFindUserByEmail() = runTest {
        val user = User(email = "test@ub.bw", passwordHash = "hash", studentId = "S001", phone = "71234567", role = "Student")
        userDao.insert(user)
        val found = userDao.findByEmail("test@ub.bw")
        assertNotNull(found)
        assertEquals("S001", found!!.studentId)
    }

    @Test fun findByEmailReturnsNullForMissing() = runTest {
        assertNull(userDao.findByEmail("nobody@ub.bw"))
    }

    @Test fun userCountIncrementsOnInsert() = runTest {
        assertEquals(0, userDao.count())
        userDao.insert(User(email = "a@b.com", passwordHash = "h", studentId = null, phone = "71000000", role = "Provider"))
        assertEquals(1, userDao.count())
    }

    // --- Listing ---

    @Test fun insertAndRetrieveListing() = runTest {
        val id = listingDao.insert(sampleListing())
        val found = listingDao.findById(id.toInt())
        assertNotNull(found)
        assertEquals("Test Room", found!!.title)
    }

    @Test fun getAllListingsReturnsInserted() = runTest {
        listingDao.insert(sampleListing())
        listingDao.insert(sampleListing().copy(title = "Room B"))
        val all = listingDao.getAll().first()
        assertEquals(2, all.size)
    }

    @Test fun updateListingStatusToReserved() = runTest {
        val id = listingDao.insert(sampleListing())
        val listing = listingDao.findById(id.toInt())!!
        listingDao.update(listing.copy(status = "Reserved"))
        assertEquals("Reserved", listingDao.findById(id.toInt())!!.status)
    }

    @Test fun filterByLocationReturnsMatchingListings() = runTest {
        listingDao.insert(sampleListing().copy(location = "Broadhurst"))
        listingDao.insert(sampleListing().copy(location = "Tlokweng"))
        val results = listingDao.filter(0.0, 0.0, "Broadhurst", 0L).first()
        assertTrue(results.all { it.location == "Broadhurst" })
    }

    @Test fun filterByPriceRangeExcludesOutOfRange() = runTest {
        listingDao.insert(sampleListing().copy(price = 800.0))
        listingDao.insert(sampleListing().copy(price = 2000.0))
        val results = listingDao.filter(1000.0, 1500.0, "", 0L).first()
        assertTrue(results.all { it.price in 1000.0..1500.0 })
    }

    // --- Reservation ---

    @Test fun insertAndFindReservationByListing() = runTest {
        val listingId = listingDao.insert(sampleListing()).toInt()
        val reservation = Reservation(listingId = listingId, studentId = 1, referenceNumber = "RES-TEST01", amount = 500.0, reservationDate = System.currentTimeMillis())
        reservationDao.insert(reservation)
        val found = reservationDao.findByListing(listingId)
        assertNotNull(found)
        assertEquals("RES-TEST01", found!!.referenceNumber)
    }

    @Test fun getByStudentReturnsOnlyThatStudentsReservations() = runTest {
        val listingId = listingDao.insert(sampleListing()).toInt()
        reservationDao.insert(Reservation(listingId = listingId, studentId = 1, referenceNumber = "RES-S1", amount = 500.0, reservationDate = 0L))
        reservationDao.insert(Reservation(listingId = listingId, studentId = 2, referenceNumber = "RES-S2", amount = 500.0, reservationDate = 0L))
        val student1Reservations = reservationDao.getByStudent(1).first()
        assertEquals(1, student1Reservations.size)
        assertEquals("RES-S1", student1Reservations[0].referenceNumber)
    }

    private fun sampleListing() = Listing(
        providerId = 1, title = "Test Room", price = 1200.0, location = "Broadhurst",
        type = "Single Room", amenities = "WiFi,Water", availabilityDate = System.currentTimeMillis(),
        deposit = 400.0, imagePath = "", status = "Available"
    )
}
