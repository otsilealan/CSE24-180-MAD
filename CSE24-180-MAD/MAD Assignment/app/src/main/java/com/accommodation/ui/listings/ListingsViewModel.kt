package com.accommodation.ui.listings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accommodation.data.database.entities.Listing
import com.accommodation.data.repository.ListingRepository
import com.accommodation.domain.CampusData
import com.accommodation.domain.FilterParams
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.math.*

class ListingsViewModel(private val repo: ListingRepository) : ViewModel() {
    private val _filter = MutableStateFlow(FilterParams())
    val filter: StateFlow<FilterParams> = _filter

    val listings: StateFlow<List<Listing>> = _filter
        .flatMapLatest { f -> repo.filter(f.minPrice, f.maxPrice, f.location, f.date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun applyFilter(params: FilterParams) { _filter.value = params }
    fun clearFilter() { _filter.value = FilterParams() }

    fun distanceFromCampusKm(listing: Listing): Double {
        val coords = CampusData.locationCoords[listing.location] ?: return -1.0
        val dLat = Math.toRadians(coords.first - CampusData.CAMPUS_LAT)
        val dLon = Math.toRadians(coords.second - CampusData.CAMPUS_LON)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(CampusData.CAMPUS_LAT)) * cos(Math.toRadians(coords.first)) *
                sin(dLon / 2).pow(2)
        return 6371.0 * 2 * atan2(sqrt(a), sqrt(1 - a))
    }

    fun insertListing(listing: Listing) = viewModelScope.launch { repo.insert(listing) }
}
