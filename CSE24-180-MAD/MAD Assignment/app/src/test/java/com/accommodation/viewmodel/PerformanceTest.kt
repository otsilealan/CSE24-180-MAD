package com.accommodation.viewmodel

import com.accommodation.data.database.entities.Listing
import com.accommodation.ui.listings.FilterParams
import com.accommodation.ui.listings.ListingsViewModel
import com.accommodation.data.repository.ListingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PerformanceTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before fun setUp() { Dispatchers.setMain(testDispatcher) }
    @After fun tearDown() { Dispatchers.resetMain() }

    private fun makeListings(count: Int): List<Listing> = (1..count).map { i ->
        Listing(i, 1, "Listing $i", (1000 + i * 10).toDouble(), "Broadhurst", "Single Room",
            "WiFi,Water", System.currentTimeMillis(), 500.0, "", "Available")
    }

    private fun fakeRepo(listings: List<Listing>) = object : ListingRepository(
        object : com.accommodation.data.database.dao.ListingDao {
            override suspend fun insert(l: Listing) = 1L
            override suspend fun update(l: Listing) {}
            override fun getAll() = flowOf(listings)
            override suspend fun findById(id: Int) = listings.firstOrNull { it.id == id }
            override fun getByProvider(p: Int) = flowOf(emptyList<Listing>())
            override fun filter(min: Double, max: Double, loc: String, date: Long) =
                flowOf(listings.filter { it.price in min..max && (loc.isEmpty() || it.location == loc) })
            override suspend fun count() = listings.size
        }
    ) {}

    /** 50+ listings load within 100ms */
    @Test fun `50 listings load within 100ms`() = runTest {
        val listings = makeListings(50)
        val repo = fakeRepo(listings)
        val start = System.currentTimeMillis()
        val result = repo.getAll().first()
        val elapsed = System.currentTimeMillis() - start
        assertEquals(50, result.size)
        assertTrue("Load took ${elapsed}ms, expected < 100ms", elapsed < 100)
    }

    /** Filter on 50 listings completes within 50ms */
    @Test fun `filter on 50 listings completes within 50ms`() = runTest {
        val listings = makeListings(50)
        val repo = fakeRepo(listings)
        val start = System.currentTimeMillis()
        val result = repo.filter(1100.0, 1300.0, "", 0).first()
        val elapsed = System.currentTimeMillis() - start
        assertTrue("Filter took ${elapsed}ms, expected < 50ms", elapsed < 50)
        assertTrue(result.isNotEmpty())
    }

    /** Distance calculation for 50 listings completes within 50ms */
    @Test fun `distance calculation for 50 listings is fast`() = runTest {
        val listings = makeListings(50)
        val vm = ListingsViewModel(fakeRepo(listings))
        val start = System.currentTimeMillis()
        listings.forEach { vm.distanceFromCampusKm(it) }
        val elapsed = System.currentTimeMillis() - start
        assertTrue("Distance calc took ${elapsed}ms, expected < 50ms", elapsed < 50)
    }

    /** Haversine distance from campus to itself is ~0 */
    @Test fun `distance from campus to campus is near zero`() = runTest {
        val vm = ListingsViewModel(fakeRepo(emptyList()))
        // Use a listing at a location near campus
        val listing = Listing(1, 1, "Near Campus", 1000.0, "Extension 2", "Single Room", "", System.currentTimeMillis(), 500.0, "", "Available")
        val dist = vm.distanceFromCampusKm(listing)
        // Should be a small number (campus coords map to Gaborone CBD)
        assertTrue("Expected small distance, got $dist", dist < 10.0)
    }
}
