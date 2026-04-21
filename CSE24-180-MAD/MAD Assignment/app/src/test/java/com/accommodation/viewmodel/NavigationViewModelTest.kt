package com.accommodation.viewmodel

import com.accommodation.data.database.entities.Listing
import com.accommodation.ui.listings.FilterParams
import com.accommodation.ui.listings.ListingsViewModel
import com.accommodation.ui.navigation.NavigationViewModel
import org.junit.Assert.*
import org.junit.Test

class NavigationViewModelTest {

    private val vm = NavigationViewModel()

    private fun listing(location: String) = Listing(1, 1, "Test", 1000.0, location, "Single Room", "WiFi", 0L, 300.0, "", "Available")

    @Test fun `distanceFromCampusKm returns positive value for known location`() {
        val dist = vm.distanceFromCampusKm(listing("Broadhurst"))
        assertTrue(dist > 0)
    }

    @Test fun `distanceFromCampusKm returns -1 for unknown location`() {
        val dist = vm.distanceFromCampusKm(listing("Unknown Area"))
        assertEquals(-1.0, dist, 0.001)
    }

    @Test fun `getListingLatLng returns coords for known location`() {
        val latLng = vm.getListingLatLng(listing("Tlokweng"))
        val expected = ListingsViewModel.campusCoords["Tlokweng"]!!
        assertEquals(expected.first, latLng.latitude, 0.0001)
        assertEquals(expected.second, latLng.longitude, 0.0001)
    }

    @Test fun `getListingLatLng falls back for unknown location`() {
        val latLng = vm.getListingLatLng(listing("Nowhere"))
        assertNotNull(latLng)
    }

    @Test fun `distance from campus to Gaborone West is reasonable`() {
        // UB campus to Gaborone West should be a few km, not hundreds
        val dist = vm.distanceFromCampusKm(listing("Gaborone West"))
        assertTrue("Expected < 20km, got $dist", dist < 20.0)
    }
}
