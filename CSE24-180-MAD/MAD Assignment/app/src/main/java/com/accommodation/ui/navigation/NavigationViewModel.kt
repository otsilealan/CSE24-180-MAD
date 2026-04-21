package com.accommodation.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accommodation.data.database.entities.Listing
import com.accommodation.data.database.entities.Reservation
import com.accommodation.data.repository.ListingRepository
import com.accommodation.domain.CampusData
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.*

class NavigationViewModel(private val listingRepo: ListingRepository) : ViewModel() {

    private val _currentListing = MutableStateFlow<Listing?>(null)
    val currentListing: StateFlow<Listing?> = _currentListing

    private val _currentReservation = MutableStateFlow<Reservation?>(null)
    val currentReservation: StateFlow<Reservation?> = _currentReservation

    fun setListing(listing: Listing) { _currentListing.value = listing }
    fun setReservation(reservation: Reservation) { _currentReservation.value = reservation }
    fun clearTransaction() { _currentListing.value = null; _currentReservation.value = null }

    fun loadListing(id: Int) = viewModelScope.launch {
        _currentListing.value = listingRepo.findById(id)
    }

    fun getListingLatLng(listing: Listing): LatLng {
        val coords = CampusData.locationCoords[listing.location]
            ?: CampusData.locationCoords.values.first()
        return LatLng(coords.first, coords.second)
    }

    fun distanceFromCampusKm(listing: Listing): Double {
        val coords = CampusData.locationCoords[listing.location] ?: return -1.0
        val dLat = Math.toRadians(coords.first - CampusData.CAMPUS_LAT)
        val dLon = Math.toRadians(coords.second - CampusData.CAMPUS_LON)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(CampusData.CAMPUS_LAT)) * cos(Math.toRadians(coords.first)) *
                sin(dLon / 2).pow(2)
        return 6371.0 * 2 * atan2(sqrt(a), sqrt(1 - a))
    }
}
