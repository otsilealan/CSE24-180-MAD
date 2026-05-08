package com.accommodation.viewmodel

import com.accommodation.data.database.entities.Listing
import com.accommodation.data.database.entities.Reservation
import com.accommodation.data.database.entities.User
import com.accommodation.data.repository.ReservationRepository
import com.accommodation.data.repository.UserRepository
import com.accommodation.ui.auth.AuthState
import com.accommodation.ui.auth.AuthViewModel
import com.accommodation.ui.reservation.ReservationState
import com.accommodation.ui.reservation.ReservationViewModel
import com.accommodation.utils.ValidationUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EdgeCaseTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before fun setUp() { Dispatchers.setMain(testDispatcher) }
    @After fun tearDown() { Dispatchers.resetMain() }

    // ── Validation edge cases ──────────────────────────────────────────────────

    @Test fun `empty email is invalid`() = assertFalse(ValidationUtils.isValidEmail(""))
    @Test fun `email without at-sign is invalid`() = assertFalse(ValidationUtils.isValidEmail("notanemail"))
    @Test fun `valid email passes`() = assertTrue(ValidationUtils.isValidEmail("student@ub.bw"))

    @Test fun `password shorter than 8 chars is invalid`() = assertFalse(ValidationUtils.isValidPassword("Ab@1"))
    @Test fun `password without special char is invalid`() = assertFalse(ValidationUtils.isValidPassword("Password1"))
    @Test fun `password without digit is invalid`() = assertFalse(ValidationUtils.isValidPassword("Password@"))
    @Test fun `valid password passes`() = assertTrue(ValidationUtils.isValidPassword("Pass@1234"))

    @Test fun `botswana phone without country code passes`() = assertTrue(ValidationUtils.isValidPhone("71234567"))
    @Test fun `botswana phone with country code passes`() = assertTrue(ValidationUtils.isValidPhone("+26771234567"))
    @Test fun `phone with wrong prefix fails`() = assertFalse(ValidationUtils.isValidPhone("61234567"))
    @Test fun `too short phone fails`() = assertFalse(ValidationUtils.isValidPhone("7123"))

    // ── Auth edge cases ────────────────────────────────────────────────────────

    private fun authRepo(
        registerResult: Result<User> = Result.success(User(1, "a@b.com", "h", "S1", "71234567", "Student")),
        loginResult: Result<User> = Result.success(User(1, "a@b.com", "h", "S1", "71234567", "Student"))
    ) = object : UserRepository(
        object : com.accommodation.data.database.dao.UserDao {
            override suspend fun insert(user: User) = 1L
            override suspend fun findByEmail(email: String) = null
            override suspend fun findById(id: Int) = null
            override suspend fun count() = 0
        }
    ) {
        override suspend fun register(email: String, password: String, studentId: String?, phone: String, role: String) = registerResult
        override suspend fun login(email: String, password: String) = loginResult
    }

    @Test fun `duplicate email registration returns error`() = runTest {
        val vm = AuthViewModel(authRepo(registerResult = Result.failure(Exception("Email already registered"))))
        vm.register("existing@b.com", "Pass@123", "S001", "71234567", "Student")
        val state = vm.state.first()
        assertTrue(state is AuthState.Error)
        assertEquals("Email already registered", (state as AuthState.Error).message)
    }

    @Test fun `login with wrong password returns error`() = runTest {
        val vm = AuthViewModel(authRepo(loginResult = Result.failure(Exception("Invalid email or password"))))
        vm.login("a@b.com", "WrongPass@1")
        assertTrue(vm.state.first() is AuthState.Error)
    }

    @Test fun `login with non-existent email returns error`() = runTest {
        val vm = AuthViewModel(authRepo(loginResult = Result.failure(Exception("Invalid email or password"))))
        vm.login("nobody@b.com", "Pass@123")
        assertTrue(vm.state.first() is AuthState.Error)
    }

    // ── Reservation edge cases ─────────────────────────────────────────────────

    private val sampleListing = Listing(1, 1, "Room A", 1500.0, "Broadhurst", "Single Room", "WiFi", System.currentTimeMillis(), 500.0, "", "Available")
    private val sampleReservation = Reservation(1, 1, 2, "RES-ABCD1234", 500.0, System.currentTimeMillis())

    private fun reservationRepo(result: Result<Reservation>) = object : ReservationRepository(
        object : com.accommodation.data.database.dao.ReservationDao {
            override suspend fun insert(r: Reservation) = 1L
            override fun getByStudent(studentId: Int) = flowOf(emptyList<Reservation>())
            override suspend fun findByListing(listingId: Int) = null
        },
        object : com.accommodation.data.database.dao.ListingDao {
            override suspend fun insert(l: Listing) = 1L
            override suspend fun update(l: Listing) {}
            override fun getAll() = flowOf(emptyList<Listing>())
            override suspend fun findById(id: Int) = sampleListing
            override fun getByProvider(p: Int) = flowOf(emptyList<Listing>())
            override fun filter(min: Double, max: Double, loc: String, date: Long) = flowOf(emptyList<Listing>())
            override suspend fun count() = 0
        }
    ) {
        override suspend fun reserve(listing: Listing, studentId: Int) = result
    }

    @Test fun `reserving already-reserved listing returns error`() = runTest {
        val vm = ReservationViewModel(reservationRepo(Result.failure(Exception("This listing has already been reserved"))))
        vm.reserve(sampleListing.copy(status = "Reserved"), 2)
        val state = vm.state.first()
        assertTrue(state is ReservationState.Error)
        assertEquals("This listing has already been reserved", (state as ReservationState.Error).message)
    }

    @Test fun `reserving non-existent listing returns error`() = runTest {
        val vm = ReservationViewModel(reservationRepo(Result.failure(Exception("Listing not found"))))
        vm.reserve(sampleListing.copy(id = 9999), 2)
        val state = vm.state.first()
        assertTrue(state is ReservationState.Error)
        assertEquals("Listing not found", (state as ReservationState.Error).message)
    }

    @Test fun `successful reservation has non-blank reference number`() = runTest {
        val vm = ReservationViewModel(reservationRepo(Result.success(sampleReservation)))
        vm.reserve(sampleListing, 2)
        val state = vm.state.first()
        assertTrue(state is ReservationState.Success)
        assertTrue((state as ReservationState.Success).reservation.referenceNumber.isNotBlank())
    }

    @Test fun `reservation amount matches listing deposit`() = runTest {
        val vm = ReservationViewModel(reservationRepo(Result.success(sampleReservation)))
        vm.reserve(sampleListing, 2)
        val state = vm.state.first() as ReservationState.Success
        assertEquals(sampleListing.deposit, state.reservation.amount, 0.001)
    }
}
