package com.accommodation.ui.listings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accommodation.data.database.entities.Listing
import com.accommodation.data.repository.ListingRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.math.*

data class FilterParams(
    val minPrice: Double = 0.0,
    val maxPrice: Double = 0.0,
    val location: String = "",
    val date: Long = 0L
)

class ListingsViewModel(private val repo: ListingRepository) : ViewModel() {
    private val _filter = MutableStateFlow(FilterParams())
    val filter: StateFlow<FilterParams> = _filter

    val listings: StateFlow<List<Listing>> = _filter
        .flatMapLatest { f -> repo.filter(f.minPrice, f.maxPrice, f.location, f.date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun applyFilter(params: FilterParams) { _filter.value = params }
    fun clearFilter() { _filter.value = FilterParams() }

    fun distanceFromCampusKm(listing: Listing): Double {
        // UB Main Campus: -24.6553, 25.9086
        val campusLat = -24.6553; val campusLon = 25.9086
        val listingCoords = campusCoords[listing.location] ?: return -1.0
        return haversine(campusLat, campusLon, listingCoords.first, listingCoords.second)
    }

    private fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
        return r * 2 * atan2(sqrt(a), sqrt(1 - a))
    }

    fun insertListing(listing: Listing) = viewModelScope.launch { repo.insert(listing) }

    companion object {
        val campusCoords = mapOf(
            "Gaborone West" to Pair(-24.6600, 25.8900),
            "Gaborone North" to Pair(-24.6200, 25.9200),
            "Broadhurst" to Pair(-24.6800, 25.9100),
            "Tlokweng" to Pair(-24.6500, 25.9700),
            "Mogoditshane" to Pair(-24.6200, 25.8500),
            "Phakalane" to Pair(-24.5800, 25.9300),
            "Block 8" to Pair(-24.6700, 25.9000),
            "Block 9" to Pair(-24.6750, 25.9050),
            "Extension 2" to Pair(-24.6550, 25.9100),
            "Bontleng" to Pair(-24.6450, 25.9150)
        )
    }
}
