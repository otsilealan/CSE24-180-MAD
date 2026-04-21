package com.accommodation.viewmodel

import com.accommodation.data.database.entities.Listing
import com.accommodation.data.database.entities.Reservation
import com.accommodation.data.repository.ReservationRepository
import com.accommodation.ui.reservation.ReservationState
import com.accommodation.ui.reservation.ReservationViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ReservationViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before fun setUp() { Dispatchers.setMain(testDispatcher) }
    @After fun tearDown() { Dispatchers.resetMain() }

    private val sampleListing = Listing(1, 1, "Test Room", 1500.0, "Broadhurst", "Single Room", "WiFi,Water", System.currentTimeMillis(), 500.0, "", "Available")
    private val sampleReservation = Reservation(1, 1, 2, "RES-ABCD1234", 500.0, System.currentTimeMillis())

    private fun fakeRepo(result: Result<Reservation>) = object : ReservationRepository(
        object : com.accommodation.data.database.dao.ReservationDao {
            override suspend fun insert(reservation: Reservation) = 1L
            override fun getByStudent(studentId: Int) = kotlinx.coroutines.flow.flowOf(emptyList<Reservation>())
            override suspend fun findByListing(listingId: Int) = null
        },
        object : com.accommodation.data.database.dao.ListingDao {
            override suspend fun insert(listing: Listing) = 1L
            override suspend fun update(listing: Listing) {}
            override fun getAll() = kotlinx.coroutines.flow.flowOf(emptyList<Listing>())
            override suspend fun findById(id: Int) = sampleListing
            override fun getByProvider(providerId: Int) = kotlinx.coroutines.flow.flowOf(emptyList<Listing>())
            override fun filter(minPrice: Double, maxPrice: Double, location: String, date: Long) = kotlinx.coroutines.flow.flowOf(emptyList<Listing>())
            override suspend fun count() = 0
        }
    ) {
        override suspend fun reserve(listing: Listing, studentId: Int) = result
    }

    @Test fun `reserve success emits Success state`() = runTest {
        val vm = ReservationViewModel(fakeRepo(Result.success(sampleReservation)))
        vm.reserve(sampleListing, 2)
        val state = vm.state.first()
        assertTrue(state is ReservationState.Success)
        assertEquals("RES-ABCD1234", (state as ReservationState.Success).reservation.referenceNumber)
    }

    @Test fun `reserve already reserved emits Error state`() = runTest {
        val vm = ReservationViewModel(fakeRepo(Result.failure(Exception("This listing has already been reserved"))))
        vm.reserve(sampleListing.copy(status = "Reserved"), 2)
        val state = vm.state.first()
        assertTrue(state is ReservationState.Error)
        assertEquals("This listing has already been reserved", (state as ReservationState.Error).message)
    }

    @Test fun `reset returns Idle state`() = runTest {
        val vm = ReservationViewModel(fakeRepo(Result.success(sampleReservation)))
        vm.reserve(sampleListing, 2)
        vm.reset()
        assertTrue(vm.state.first() is ReservationState.Idle)
    }
}
