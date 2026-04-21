package com.accommodation.ui.navigation

import androidx.lifecycle.ViewModel
import com.accommodation.data.database.entities.Listing
import com.accommodation.ui.listings.ListingsViewModel
import com.google.android.gms.maps.model.LatLng
import kotlin.math.*

class NavigationViewModel : ViewModel() {

    fun getListingLatLng(listing: Listing): LatLng {
        val coords = ListingsViewModel.campusCoords[listing.location]
            ?: ListingsViewModel.campusCoords.values.first()
        return LatLng(coords.first, coords.second)
    }

    fun distanceFromCampusKm(listing: Listing): Double {
        val coords = ListingsViewModel.campusCoords[listing.location] ?: return -1.0
        val campusLat = -24.6553; val campusLon = 25.9086
        val dLat = Math.toRadians(coords.first - campusLat)
        val dLon = Math.toRadians(coords.second - campusLon)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(campusLat)) * cos(Math.toRadians(coords.first)) *
                sin(dLon / 2).pow(2)
        return 6371.0 * 2 * atan2(sqrt(a), sqrt(1 - a))
    }
}
