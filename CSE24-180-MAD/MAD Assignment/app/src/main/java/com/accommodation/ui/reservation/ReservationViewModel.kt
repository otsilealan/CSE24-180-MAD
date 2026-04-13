package com.accommodation.ui.reservation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accommodation.data.database.entities.Listing
import com.accommodation.data.database.entities.Reservation
import com.accommodation.data.repository.ReservationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ReservationState {
    object Idle : ReservationState()
    object Loading : ReservationState()
    data class Success(val reservation: Reservation) : ReservationState()
    data class Error(val message: String) : ReservationState()
}

class ReservationViewModel(private val repo: ReservationRepository) : ViewModel() {
    private val _state = MutableStateFlow<ReservationState>(ReservationState.Idle)
    val state: StateFlow<ReservationState> = _state

    fun reserve(listing: Listing, studentId: Int) {
        viewModelScope.launch {
            _state.value = ReservationState.Loading
            _state.value = repo.reserve(listing, studentId)
                .fold({ ReservationState.Success(it) }, { ReservationState.Error(it.message ?: "Reservation failed") })
        }
    }

    fun reset() { _state.value = ReservationState.Idle }
}
