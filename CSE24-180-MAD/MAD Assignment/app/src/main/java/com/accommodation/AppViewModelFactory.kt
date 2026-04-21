package com.accommodation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.accommodation.data.AppContainer
import com.accommodation.ui.auth.AuthViewModel
import com.accommodation.ui.filter.FilterViewModel
import com.accommodation.ui.listings.ListingsViewModel
import com.accommodation.ui.navigation.NavigationViewModel
import com.accommodation.ui.reservation.ReservationViewModel

class AppViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when (modelClass) {
        AuthViewModel::class.java -> AuthViewModel(container.userRepository)
        ListingsViewModel::class.java -> ListingsViewModel(container.listingRepository)
        FilterViewModel::class.java -> FilterViewModel(container.preferencesRepository)
        ReservationViewModel::class.java -> ReservationViewModel(container.reservationRepository)
        NavigationViewModel::class.java -> NavigationViewModel(container.listingRepository)
        else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    } as T
}
