package com.accommodation.viewmodel

import com.accommodation.data.database.entities.Listing
import com.accommodation.data.repository.ListingRepository
import com.accommodation.ui.listings.FilterParams
import com.accommodation.ui.listings.ListingsViewModel
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
class ListingsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before fun setUp() { Dispatchers.setMain(testDispatcher) }
    @After fun tearDown() { Dispatchers.resetMain() }

    private val listings = listOf(
        Listing(1, 1, "Room A", 1200.0, "Broadhurst", "Single Room", "WiFi", 0L, 400.0, "", "Available"),
        Listing(2, 1, "Room B", 2500.0, "Tlokweng", "1-Bedroom", "WiFi,Parking", 0L, 800.0, "", "Available")
    )

    private fun fakeRepo(result: List<Listing> = listings) = object : ListingRepository(
        object : com.accommodation.data.database.dao.ListingDao {
            override suspend fun insert(listing: Listing) = 1L
            override suspend fun update(listing: Listing) {}
            override fun getAll() = flowOf(result)
            override suspend fun findById(id: Int) = result.firstOrNull { it.id == id }
            override fun getByProvider(providerId: Int) = flowOf(result.filter { it.providerId == providerId })
            override fun filter(minPrice: Double, maxPrice: Double, location: String, date: Long) = flowOf(result)
            override suspend fun count() = result.size
        }
    ) {}

    @Test fun `applyFilter updates filter state`() = runTest {
        val vm = ListingsViewModel(fakeRepo())
        val params = FilterParams(minPrice = 1000.0, maxPrice = 2000.0, location = "Broadhurst")
        vm.applyFilter(params)
        assertEquals(params, vm.filter.first())
    }

    @Test fun `clearFilter resets to default FilterParams`() = runTest {
        val vm = ListingsViewModel(fakeRepo())
        vm.applyFilter(FilterParams(minPrice = 500.0))
        vm.clearFilter()
        assertEquals(FilterParams(), vm.filter.first())
    }

    @Test fun `distanceFromCampusKm returns positive for known location`() {
        val vm = ListingsViewModel(fakeRepo())
        val dist = vm.distanceFromCampusKm(listings[0])
        assertTrue(dist > 0)
    }

    @Test fun `distanceFromCampusKm returns -1 for unknown location`() {
        val vm = ListingsViewModel(fakeRepo())
        val unknown = listings[0].copy(location = "Unknown")
        assertEquals(-1.0, vm.distanceFromCampusKm(unknown), 0.001)
    }
}
